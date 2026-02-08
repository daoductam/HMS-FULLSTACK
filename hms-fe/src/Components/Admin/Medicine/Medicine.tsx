import {
  ActionIcon,
  Button,
  Fieldset,
  MultiSelect,
  NumberInput,
  SegmentedControl,
  Select,
  Textarea,
  TextInput,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import {
  dosageFrequencies,
  medicineCategories,
  medicineTypes,
  symptoms,
  tests,
} from "../../../Data/DropDownData";
import {
  IconEdit,
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
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { Column } from "primereact/column";
import { formatDate } from "../../../Utility/DateUtility";
import {
  addMedicine,
  getAllMedicines,
  updateMedicine,
} from "../../../Service/MedicineService";
import { getLabel } from "../../../Utility/OtherUtility";
import { Toolbar } from "primereact/toolbar";
import ReportCard from "../../Doctor/Appointment/ReportCard";
import MedCard from "./MedCard";
import { useMediaQuery } from "@mantine/hooks";

type Medicine = {
  name: string;
  medicine?: number;
  dosage: string;
  frequency: string;
  duration: number;
  route: string;
  type: string;
  instructions: string;
  prescriptionId?: number;
};
const Medicine = () => {
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    name: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    category: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    notes: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    manufacturer: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
  });

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
  const [view, setView] = useState("table");
  const [edit, setEdit] = useState<Boolean>(false);
  const matches = useMediaQuery("(max-width: 768px)");

  const [loading, setLoading] = useState(false);
  const form = useForm({
    initialValues: {
      id: null,
      name: "",
      dosage: "",
      category: "",
      type: "",
      manufacturer: "",
      unitPrice: "",
    },

    validate: {
      name: (value: any) => (!value ? "Tên không được để trống" : null),
      dosage: (value: any) =>
        !value ? "Liều lượng không được để trống" : null,
      category: (value: any) =>
        !value ? "Danh mục không được để trống" : null,
      type: (value: any) => (!value ? "Loại không được để trống" : null),
      manufacturer: (value: any) =>
        !value ? "Nhà sản xuất không được để trống" : null,
      unitPrice: (value: any) =>
        !value
          ? "Giá không được để trống"
          : Number(value) <= 0
          ? "Giá phải là số dương"
          : null,
    },
  });

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

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = () => {
    // if (!appointment?.patientId) return;

    getAllMedicines()
      .then((res) => {
        console.log("Report Data: ", res);
        setData(res);
      })
      .catch((err) => {
        console.log("Error fetching request: ", err);
      });
  };

  const onEdit = (rowData: any) => {
    setEdit(true);
    form.setValues({
      ...rowData,
      name: rowData.name,
      dosage: rowData.dosage,
      category: rowData.category,
      type: rowData.type,
      manufacturer: rowData.manufacturer,
      unitPrice: rowData.unitPrice,
    });
  };

  const handleSubmit = (values: any) => {
    let update = false;
    let method;
    if (values.id) {
      update = true;
      method = updateMedicine;
    } else {
      method = addMedicine;
    }
    setLoading(true);
    method(values)
      .then((_res) => {
        successNotification(`${update ? "Cập nhật" : "Thêm"} thuốc thành công`);
        form.reset();
        setEdit(false);
        fetchData();
      })
      .catch((error) => {
        errorNotification(
          error?.response?.data?.message ||
            `${update ? "Cập nhật" : "Thêm"} thuốc thất bại`
        );
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const cancel = () => {
    form.reset();
    setEdit(false);
  };

  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between items-center">
        <Button variant="filled" onClick={() => setEdit(true)}>
          Thêm Thuốc
        </Button>

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

  const startToolBarTemplate = () => {
    return (
      <Button variant="filled" onClick={() => setEdit(true)}>
        Thêm Thuốc
      </Button>
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
  const actionBodyTemplate = (rowData: any) => {
    return (
      <div className="flex gap-2 ">
        <ActionIcon onClick={() => onEdit(rowData)}>
          <IconEdit size={20} stroke={1.5}></IconEdit>
        </ActionIcon>
      </div>
    );
  };
  const header = renderHeader();

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
              filters={filters}
              globalFilterFields={["name", "manufacturer", "notes", "category"]}
              emptyMessage="Không cuộc hẹn nào được tìm thấy."
              currentPageReportTemplate="{first} - {last} của {totalRecords} mục"
            >
              {/* <Column
            selectionMode="multiple"
            headerStyle={{ width: "3rem" }}
          ></Column> */}
              <Column field="name" header="Tên" sortable />
              <Column field="dosage" header="Liều lượng" />
              <Column field="stock" header="Hàng tồn" />
              <Column
                field="category"
                header="Danh mục"
                body={(rowData) =>
                  getLabel(medicineCategories, rowData.category)
                }
              />
              <Column
                field="type"
                header="Loại"
                body={(rowData) => getLabel(medicineTypes, rowData.type)}
              />
              <Column field="manufacturer" header="Nhà SX" />

              <Column
                field="unitPrice"
                header="Giá"
                body={(rowData) => rowData.unitPrice}
              />
              <Column field="notes" header="Ghi chú" />
              <Column
                headerStyle={{ textAlign: "center" }}
                bodyStyle={{ textAlign: "center", overflow: "visible" }}
                body={actionBodyTemplate}
              />
            </DataTable>
          ) : (
            <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5">
              {data?.map((appointment) => (
                <MedCard
                  key={appointment.id}
                  {...appointment}
                  onEdit={() => onEdit(appointment)}
                />
              ))}
              {data.length === 0 && (
                <div className="col-span-4 text-center text-gray-500">
                  Không có thuốc nào được tìm thấy.
                </div>
              )}
            </div>
          )}
        </div>
      ) : (
        <form onSubmit={form.onSubmit(handleSubmit)} className="grid gap-5">
          <Fieldset
            className="grid gap-4 sm:grid-cols-2"
            legend={
              <span className="text-lg font-medium text-primary-5">
                Thông tin Thuốc
              </span>
            }
            radius="md"
          >
            <TextInput
              {...form.getInputProps("name")}
              label="Thuốc"
              placeholder="Nhập tên thuốc"
              withAsterisk
            />
            <TextInput
              {...form.getInputProps("dosage")}
              label="Liều lượng"
              placeholder="Nhập liều lượng (50mg, 100mg,...)"
            />
            <Select
              {...form.getInputProps("category")}
              label="Danh mục"
              placeholder="Chọn danh mục"
              data={medicineCategories}
            />
            <Select
              {...form.getInputProps("type")}
              label="Loại"
              placeholder="Chọn loại"
              data={medicineTypes}
            />
            <TextInput
              {...form.getInputProps("manufacturer")}
              label="Nhà SX"
              placeholder="Nhập nhà sx"
            />
            <NumberInput
              {...form.getInputProps("unitPrice")}
              label="Giá"
              placeholder="Nhập giá"
              min={0}
              clampBehavior="strict"
            />
          </Fieldset>

          <div className="flex items-center gap-5 justify-center">
            <Button
              loading={loading}
              type="submit"
              className="w-full"
              variant="filled"
              color="primary"
            >
              {form.values?.id ? "Cập nhật" : "Thêm"} thuốc
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

export default Medicine;
