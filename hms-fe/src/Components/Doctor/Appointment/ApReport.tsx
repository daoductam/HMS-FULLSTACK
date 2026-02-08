import {
  ActionIcon,
  Button,
  Fieldset,
  Group,
  MultiSelect,
  NumberInput,
  SegmentedControl,
  Select,
  SelectProps,
  Textarea,
  TextInput,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import {
  dosageFrequencies,
  medicineTypes,
  symptoms,
  tests,
} from "../../../Data/DropDownData";
import {
  IconCheck,
  IconEye,
  IconLayoutGrid,
  IconSearch,
  IconTable,
  IconTrash,
} from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import {
  createAppointmentReport,
  getReportsByPatientId,
  isReportExists,
} from "../../../Service/AppointmentService";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { useDispatch } from "react-redux";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { useNavigate } from "react-router-dom";
import { FilterMatchMode } from "primereact/api";
import { Column } from "primereact/column";
import { formatDate } from "../../../Utility/DateUtility";
import { getAllMedicines } from "../../../Service/MedicineService";
import { Toolbar } from "primereact/toolbar";
import ReportCard from "./ReportCard";
import { useMediaQuery } from "@mantine/hooks";

type Medicine = {
  name: string;
  medicineId?: string | number | undefined;
  dosage: string;
  frequency: string;
  duration: number;
  route: string;
  type: string;
  instructions: string;
  prescriptionId?: number;
};
const ApReport = ({ appointment }: any) => {
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const [view, setView] = useState("table");
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [data, setData] = useState<any[]>([]);
  const [allowAdd, setAllowAdd] = useState<Boolean>(false);
  const [edit, setEdit] = useState<Boolean>(false);

  const [loading, setLoading] = useState(false);
  const [medicine, setMedicine] = useState<any[]>([]);

  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});
  const matches = useMediaQuery("(max-width: 768px)");

  const form = useForm({
    initialValues: {
      symptoms: [],
      tests: [],
      diagnosis: "",
      referral: "",
      notes: "",
      prescription: {
        medicines: [] as Medicine[],
      },
    },
    validate: {
      symptoms: (value: any) =>
        value.length > 0 ? null : "Chọn ít nhất 1 triệu chứng",
      diagnosis: (value: any) => (value?.trim() ? null : "Cần điền chẩn đoán"),
      prescription: {
        medicines: {
          name: (value: any) => (value?.trim() ? null : "Cần điền tên thuốc"),
          dosage: (value: any) =>
            value?.trim() ? null : "Cần điền liều lượng",
          frequency: (value: any) => (value ? null : "Cần điền tần suất"),
          duration: (value: any) =>
            value > 0 ? null : "Thời gian phải lớn hơn 0",
          // route: (value: any) => (value ? null : "Cần điền route"),
          type: (value: any) => (value ? null : "Cần điền loại"),
          instructions: (value: any) =>
            value?.trim() ? null : "Cần điền hướng dẫn",
        },
      },
    },
  });

  useEffect(() => {
    getAllMedicines()
      .then((res) => {
        setMedicine(res);
        setMedicineMap(
          res.reduce((acc: any, item: any) => {
            acc[item.id] = item;
            return acc;
          }, {})
        );
      })
      .catch((err) => {
        console.log("Error fetching request: ", err);
      });
  }, []);

  const insertMedicine = () => {
    form.insertListItem("prescription.medicines", {
      name: "",
      dosage: "",
      frequency: "",
      duration: 0,
      route: "",
      type: "",
      instructions: "",
    });
  };

  const removeMedicine = (index: number) => {
    form.removeListItem("prescription.medicines", index);
  };

  useEffect(() => {
    fetchData();
  }, [appointment?.patientId, appointment.id]);

  const fetchData = () => {
    if (!appointment?.patientId) return;

    getReportsByPatientId(appointment.patientId)
      .then((res) => {
        console.log("Report Data: ", res);
        setData(res);
      })
      .catch((err) => {
        console.log("Error fetching request: ", err);
      });
    isReportExists(appointment.id)
      .then((res) => {
        setAllowAdd(!res);
      })
      .catch((err) => {
        console.error("error checking report existence: ", err);
        setAllowAdd(true);
      });
  };
  const renderSelectOption: SelectProps["renderOption"] = ({
    option,
    checked,
  }: any) => (
    <Group flex="1" gap="xs">
      <div className="flex gap-2 items-center">
        {option.label}
        {option?.manufacturer && (
          <span
            style={{ marginLeft: "auto", fontSize: "0.8rem", color: "gray" }}
          >
            {option.manufacturer} - {option.dosage}
          </span>
        )}
      </div>
      {checked && <IconCheck style={{ marginInlineStart: "auto" }} />}
    </Group>
  );

  const handleSubmit = (values: typeof form.values) => {
    let data = {
      ...values,
      doctorId: appointment.doctorId,
      patientId: appointment.patientId,
      appointmentId: appointment.id,
      prescription: {
        medicines: values.prescription.medicines.map((med) => ({
          ...med,
          medicineId: med.medicineId === "OTHER" ? null : med.medicineId,
        })),
        doctorId: appointment.doctorId,
        patientId: appointment.patientId,
        appointmentId: appointment.id,
      },
    };
    setLoading(true);
    createAppointmentReport(data)
      .then((res) => {
        successNotification("Tạo báo cáo thành công");
        form.reset();
        setEdit(false);
        setAllowAdd(false);
        fetchData();
      })
      .catch((error) => {
        errorNotification(error?.response?.data?.message);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleChangeMed = (medId: any, index: number) => {
    if (medId && medId !== "OTHER") {
      const med = medicineMap[medId];
      if (!med) return;

      form.setFieldValue(
        `prescription.medicines.${index}.medicineId`,
        String(med.id)
      );
      form.setFieldValue(
        `prescription.medicines.${index}.name`,
        med.name || ""
      );
      form.setFieldValue(
        `prescription.medicines.${index}.dosage`,
        med.dosage || ""
      );
      form.setFieldValue(
        `prescription.medicines.${index}.type`,
        med.type || ""
      );
    } else {
      form.setFieldValue(`prescription.medicines.${index}.medicineId`, "OTHER");
      form.setFieldValue(`prescription.medicines.${index}.name`, "");
      form.setFieldValue(`prescription.medicines.${index}.dosage`, "");
      form.setFieldValue(`prescription.medicines.${index}.type`, "");
    }
  };

  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between items-center">
        {allowAdd && (
          <Button variant="filled" onClick={() => setEdit(true)}>
            Thêm báo cáo
          </Button>
        )}
        <TextInput
          fw={500}
          leftSection={<IconSearch />}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
      </div>
    );
  };
  const actionBodyTemplate = (rowData: any) => {
    return (
      <div className="flex gap-2 ">
        {/* <ActionIcon
          onClick={() =>
            navigate("/doctor/appointments/" + rowData.appointmentId)
          }
        >
          <IconEye size={20} stroke={1.5}></IconEye>
        </ActionIcon> */}
      </div>
    );
  };

  const startToolBarTemplate = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-start items-center">
        {allowAdd && (
          <Button variant="filled" onClick={() => setEdit(true)}>
            Thêm báo cáo
          </Button>
        )}
      </div>
    );
  };

  const rightToolbarTemplate = () => {
    return (
      <div className="md:flex hidden flex-wrap gap-2 justify-end items-center">
        <SegmentedControl
          value={view}
          size={matches ? "xs" : "md"}
          color="primary"
          onChange={setView}
          data={[
            { label: <IconTable />, value: "table" },
            { label: <IconLayoutGrid />, value: "card" },
          ]}
        />
        <TextInput
          className="lg:block hidden"
          fw={500}
          leftSection={<IconSearch />}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
      </div>
    );
  };
  const header = renderHeader();
  const cancel = () => {
    form.reset();
    setEdit(false);
  };

  return (
    <div className="">
      {!edit ? (
        <div className="">
          <Toolbar
            className="mb-4 !p-1"
            start={startToolBarTemplate}
            end={rightToolbarTemplate}
          ></Toolbar>
          {view == "table" && !matches ? (
            <DataTable
              stripedRows
              value={data}
              size="small"
              paginator
              rows={10}
              paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
              rowsPerPageOptions={[10, 25, 50]}
              dataKey="id"
              filterDisplay="menu"
              globalFilterFields={["doctorName", "notes"]}
              emptyMessage="Không cuộc hẹn nào được tìm thấy."
              currentPageReportTemplate="{first} - {last} của {totalRecords} mục"
            >
              <Column
                selectionMode="multiple"
                headerStyle={{ width: "3rem" }}
              ></Column>
              <Column field="doctorName" header="Bác sĩ" sortable />
              <Column field="diagnosis" header="Chẩn đoán" />
              <Column
                field="reportDate"
                header="Ngày tạo báo cáo"
                body={(rowData) => formatDate(rowData.createdAt)}
              />
              <Column field="notes" header="Ghi chú" />
              <Column
                headerStyle={{ width: "5rem", textAlign: "center" }}
                bodyStyle={{ textAlign: "center", overflow: "visible" }}
                body={actionBodyTemplate}
              />
            </DataTable>
          ) : (
            <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5">
              {data?.map((appointment) => (
                <ReportCard key={appointment.id} {...appointment} />
              ))}
              {data.length === 0 && (
                <div className="col-span-4 text-center text-gray-500">
                  Không có đơn thuốc nào được tìm thấy.
                </div>
              )}
            </div>
          )}
        </div>
      ) : (
        <form onSubmit={form.onSubmit(handleSubmit)} className="grid gap-5">
          <Fieldset
            className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5"
            legend={
              <span className="text-lg font-medium text-primary-5">
                Thông tin cá nhân
              </span>
            }
            radius="md"
          >
            <MultiSelect
              {...form.getInputProps("symptoms")}
              className="col-span-2"
              withAsterisk
              label="Triệu chứng"
              placeholder="Chọn triệu chứng"
              data={symptoms}
            />
            <MultiSelect
              {...form.getInputProps("tests")}
              className="col-span-2"
              label="Xét nghiệm"
              placeholder="Chọn xét nghiệm"
              data={tests}
            />
            <TextInput
              {...form.getInputProps("diagnosis")}
              className="col-span-2 sm:col-span-1"
              label="Chẩn đoán"
              placeholder="Nhập chẩn đoán"
              withAsterisk
            />
            <TextInput
              {...form.getInputProps("referral")}
              className="col-span-2 sm:col-span-1"
              label="Chuyển tuyến"
              placeholder="Nhập thông tin giới thiệu hoặc chuyển tuyến"
            />
            <Textarea
              {...form.getInputProps("notes")}
              className="col-span-2"
              label="Ghi chú"
              placeholder="Nhập ghi chú (tùy chọn)"
            />
          </Fieldset>

          <Fieldset
            className="grid gap-5"
            legend={
              <span className="text-lg font-medium text-primary-5">
                Đơn thuốc
              </span>
            }
            radius="md"
          >
            {form.values.prescription.medicines.map(
              (med: Medicine, index: number) => (
                <Fieldset
                  key={index}
                  legend={
                    <div className="flex items-center gap-5">
                      <h1 className="text-lg font-medium">Thuốc {index + 1}</h1>
                      <ActionIcon
                        onClick={() => removeMedicine(index)}
                        variant="filled"
                        color="red"
                        size="md"
                        className="mb-2"
                      >
                        <IconTrash />
                      </ActionIcon>
                    </div>
                  }
                  className="grid gap-4 col-span-2 sm:grid-cols-2"
                >
                  <Select
                    renderOption={renderSelectOption}
                    {...form.getInputProps(
                      `prescription.medicines.${index}.medicineId`
                    )}
                    label="Thuốc"
                    placeholder="Chọn thuốc"
                    onChange={(value: any) => {
                      form.setFieldValue(
                        `prescription.medicines.${index}.medicineId`,
                        value
                      );
                      handleChangeMed(value, index);
                    }}
                    data={[
                      ...medicine
                        .filter(
                          (x: any) =>
                            !form.values.prescription.medicines.some(
                              (item1: any, idx) =>
                                item1.medicineId == x.id && idx != index
                            )
                        )
                        .map((item) => ({
                          ...item,
                          value: String(item.id),
                          label: item.name,
                        })),
                      { label: "Other", value: "OTHER" },
                    ]}
                    withAsterisk
                  />
                  {med.medicineId == "OTHER" && (
                    <TextInput
                      {...form.getInputProps(
                        `prescription.medicines.${index}.name`
                      )}
                      label="Thuốc"
                      placeholder="Nhập tên thuốc"
                      withAsterisk
                    />
                  )}
                  <TextInput
                    disabled={med.medicineId != "OTHER"}
                    {...form.getInputProps(
                      `prescription.medicines.${index}.dosage`
                    )}
                    label="Liều lượng"
                    placeholder="Nhập liều lượng"
                    withAsterisk
                  />
                  <Select
                    {...form.getInputProps(
                      `prescription.medicines.${index}.frequency`
                    )}
                    label="Tần suất"
                    placeholder="Chọn tần suất dùng thuốc"
                    withAsterisk
                    data={dosageFrequencies}
                  />
                  <NumberInput
                    {...form.getInputProps(
                      `prescription.medicines.${index}.duration`
                    )}
                    label="Thời gian (ngày)"
                    placeholder="Nhập số ngày dùng thuốc"
                    withAsterisk
                  />
                  {/* <Select
                    {...form.getInputProps(
                      `prescription.medicines.${index}.route`
                    )}
                    label="Đường dùng"
                    placeholder="Chọn đường dùng"
                    withAsterisk
                    data={["Uống", "Truyền tĩnh mạch", "Bôi ngoài da", "Hít"]}
                  /> */}

                  <Select
                    {...form.getInputProps(
                      `prescription.medicines.${index}.type`
                    )}
                    label="Dạng thuốc"
                    disabled={med.medicineId != "OTHER"}
                    placeholder="Chọn dạng thuốc"
                    withAsterisk
                    data={medicineTypes}
                  />
                  <TextInput
                    {...form.getInputProps(
                      `prescription.medicines.${index}.instructions`
                    )}
                    label="Hướng dẫn sử dụng thuốc"
                    placeholder="Nhập hướng dẫn sử dụng thuốc"
                    withAsterisk
                  />
                </Fieldset>
              )
            )}

            <div className="flex items-start col-span-2 justify-center">
              <Button
                onClick={insertMedicine}
                variant="outline"
                color="primary"
                className="col-span-2"
              >
                Thêm thuốc
              </Button>
            </div>
          </Fieldset>
          <div className="flex items-center gap-5 justify-center">
            <Button
              loading={loading}
              type="submit"
              className="w-full"
              variant="filled"
              color="primary"
            >
              Lưu báo cáo
            </Button>
            <Button
              loading={loading}
              onClick={cancel}
              variant="filled"
              color="red"
            >
              Hủy
            </Button>
          </div>
        </form>
      )}
    </div>
  );
};

export default ApReport;
