import {
  ActionIcon,
  Badge,
  Button,
  Card,
  Divider,
  Fieldset,
  Grid,
  Group,
  Loader,
  LoadingOverlay,
  Modal,
  MultiSelect,
  NumberInput,
  SegmentedControl,
  Select,
  SelectProps,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import {
  dosageFrequencies,
  freqMap,
  medicineCategories,
  medicineTypes,
  symptoms,
  tests,
} from "../../../Data/DropDownData";
import {
  IconCheck,
  IconEdit,
  IconEye,
  IconHome,
  IconLayoutGrid,
  IconPlus,
  IconSearch,
  IconTable,
  IconTrash,
} from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import {
  createAppointmentReport,
  getAllPrescriptions,
  getMedicinesByPrescriptionId,
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
import {
  addMedicine,
  getAllMedicines,
  updateMedicine,
} from "../../../Service/MedicineService";
import { getLabel } from "../../../Utility/OtherUtility";
import { DateInput } from "@mantine/dates";
import {
  addSale,
  getAllSaleItems,
  getAllSales,
  getSale,
} from "../../../Service/SalesService";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { spotlight, Spotlight, SpotlightActionData } from "@mantine/spotlight";
import SaleCard from "./SaleCard";
import { Toolbar } from "primereact/toolbar";

interface SaleItem {
  medicineId: string;
  quantity: number;
}

const Sales = () => {
  const [view, setView] = useState("table");
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
  const matches = useMediaQuery("(max-width: 768px)");

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
  const [medicine, setMedicine] = useState<any[]>([]);
  const [edit, setEdit] = useState<Boolean>(false);

  const [loading, setLoading] = useState(false);
  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});
  const [opened, { open, close }] = useDisclosure(false);
  const [saleItems, setSaleItems] = useState<any[]>([]);
  const [actions, setActions] = useState<SpotlightActionData[]>([]);

  const form = useForm({
    initialValues: {
      buyerName: "",
      buyerContact: "",
      paymentMethod: "DIRECT", // Mặc định là thanh toán trực tiếp
      saleItems: [{ medicineId: "", quantity: 0 }] as SaleItem[],
    },

    validate: {
      saleItems: {
        medicineId: (value: any) => (!value ? "Chưa chọn thuốc" : null),
        quantity: (value: any) =>
          value <= 0 ? "Số lượng phải lớn hơn 0" : null,
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
    getAllPrescriptions()
      .then((res) => {
        const sortedPrescriptions = res // Sắp xếp: Đơn mới nhất (ngày lớn nhất) lên đầu
          .sort(
            (a: any, b: any) =>
              new Date(b.prescriptionDate).getTime() -
              new Date(a.prescriptionDate).getTime()
          );

        setActions(
          // Map từ danh sách đã sắp xếp
          sortedPrescriptions.map((item: any) => ({
            id: String(item.id),
            label: item.patientName,
            description: `Dr. ${item.doctorName} - ${formatDate(
              item.prescriptionDate
            )}`,
            onClick: () => handleImport(item),
          }))
        );
      })
      .catch((err) => {
        console.log("Error fetching prescriptions: ", err);
      });
    fetchData();
  }, []);

  const handleImport = (item: any) => {
    console.log("Prescription item:", item);

    setLoading(true);
    getMedicinesByPrescriptionId(item.id)
      .then((res) => {
        // setEdit(true);
        setSaleItems(res);
        form.setValues({
          buyerName: item.patientName,
          buyerContact: form.values.buyerContact, // Giữ giá trị hiện tại
          paymentMethod: form.values.paymentMethod, // Giữ giá trị hiện tại
          saleItems: res
            .filter((x: any) => x.medicineId != null)
            .map((x: any) => ({
              medicineId: String(x.medicineId),
              quantity: calculateQuantity(x.frequency, x.duration),
            })),
        });

        console.log("Medicine response:", res);
      })
      .catch((err) => {
        console.log("Error fetching request: ", err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const calculateQuantity = (freq: string, duration: number) => {
    const freqValue = freqMap[freq] || 0;
    return Math.ceil(freqValue * duration);
  };

  const fetchData = () => {
    getAllSales()
      .then((res) => {
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
      medicineId: String(rowData.medicineId),
      batchNo: rowData.batchNo,
      quantity: rowData.quantity,
      expiryDate: new Date(rowData.expiryDate),
    });
  };

  const handleDetails = (rowData: any) => {
    open();
    setLoading(true);
    getAllSaleItems(rowData.id)
      .then((res) => {
        setSaleItems(res);
        console.log(res);
      })
      .catch((err) => {
        console.error("error fetching sale items:", err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleSubmit = (values: any) => {
    let update = false;
    let flag = false;
    values.saleItems.forEach((item: any, index: number) => {
      if (item.quantity > (medicineMap[item.medicineId]?.stock || 0)) {
        flag = true;
        form.setFieldError(
          `saleItems.${index}.quantity`,
          "Số lượng vượt quá hàng tồn kho hiện có"
        );
      }
      if (flag) {
        errorNotification("Số lượng vượt quá hàng tồn kho hiện có");
        return;
      }
    });
    const saleItems = values.saleItems.map((x: any) => ({
      ...x,
      unitPrice: medicineMap[x.medicineId]?.unitPrice,
    }));
    const totalAmount = saleItems.reduce(
      (acc: number, item: any) => acc + item.unitPrice * item.quantity,
      0
    );

    setLoading(true);
    addSale({ ...values, saleItems, totalAmount })
      .then((res) => {
        // Nếu là thanh toán MOMO và có paymentUrl, chuyển đến trang thanh toán
        if (values.paymentMethod === "MOMO" && res.paymentUrl && res.saleId) {
          navigate(
            `/admin/payment?paymentUrl=${encodeURIComponent(
              res.paymentUrl
            )}&saleId=${res.saleId}`
          );
        } else {
          // Thanh toán trực tiếp hoặc không có paymentUrl
          successNotification("Đã bán thuốc thành công");
          form.reset();
          form.setFieldValue("paymentMethod", "DIRECT"); // Reset về mặc định
          setEdit(false);
          fetchData();
        }
      })
      .catch((error) => {
        errorNotification(
          error?.response?.data?.message || "Không bán được thuốc"
        );
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const cancel = () => {
    form.reset();
    form.setFieldValue("paymentMethod", "DIRECT"); // Reset về mặc định
    setEdit(false);
  };

  const addMore = () => {
    form.insertListItem("saleItems", { medicineId: "", quantity: 0 });
  };

  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between items-center">
        <Button variant="filled" onClick={() => setEdit(true)}>
          Bán Thuốc
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
        Bán Thuốc
      </Button>
    );
  };

  const rightToolbarTemplate = () => {
    return (
      <div className="md:flex hidden flex-wrap gap-2 justify-end items-center">
        <SegmentedControl
          size={matches ? "xs" : "md"}
          value={view}
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
        <ActionIcon onClick={() => handleDetails(rowData)}>
          <IconEye size={20} stroke={1.5}></IconEye>
        </ActionIcon>
      </div>
    );
  };
  const header = renderHeader();

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

  const statusBody = (rowData: any) => {
    const isExpired = new Date(rowData.expiryDate) < new Date();
    return (
      <Badge color={isExpired ? "red" : "green"}>
        {isExpired ? "Hết hạn" : "Còn hạn"}
      </Badge>
    );
  };

  const handleSpotlight = () => {
    spotlight.open();
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
              removableSort
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
              {/* <Column
            selectionMode="multiple"
            headerStyle={{ width: "3rem" }}
          ></Column> */}
              <Column field="buyerName" header="Người mua" />
              <Column field="buyerContact" header="Liên hệ" />
              {/* <Column field="prescription" header="Số lượng" /> */}
              <Column field="totalAmount" header="Tổng tiền" sortable />
              <Column
                field="saleDate"
                header="Ngày bán"
                sortable
                body={(rowData) => formatDate(rowData.saleDate)}
              />

              <Column
                headerStyle={{ textAlign: "center" }}
                bodyStyle={{ textAlign: "center", overflow: "visible" }}
                body={actionBodyTemplate}
              />
            </DataTable>
          ) : (
            <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5">
              {data?.map((appointment) => (
                <SaleCard
                  key={appointment.id}
                  {...appointment}
                  onView={() => handleDetails(appointment)}
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
        <div className="">
          <div className="mb-5 flex items-center justify-between">
            <h3 className="text-xl text-primary-4 font-medium">Bán thuốc</h3>
            <Button
              variant="filled"
              leftSection={<IconPlus />}
              onClick={handleSpotlight}
            >
              Nhập đơn thuốc
            </Button>
          </div>
          <form onSubmit={form.onSubmit(handleSubmit)} className="grid gap-5">
            <LoadingOverlay visible={loading} />
            <Fieldset
              className="grid gap-5"
              legend={
                <span className="text-lg font-medium text-primary-5">
                  Thông tin người mua
                </span>
              }
              radius="md"
            >
              <div className="grid sm:grid-cols-2 gap-5">
                <TextInput
                  withAsterisk
                  label="Tên người mua"
                  placeholder="Nhập tên người mua"
                  {...form.getInputProps("buyerName")}
                />
                <NumberInput
                  maxLength={10}
                  label="Thông tin liên hệ"
                  placeholder="Nhập thông tin liên hệ"
                  {...form.getInputProps("buyerContact")}
                />
              </div>
            </Fieldset>
            <Fieldset
              className="grid gap-5"
              legend={
                <span className="text-lg font-medium text-primary-5">
                  Phương thức Thanh toán
                </span>
              }
              radius="md"
            >
              <SegmentedControl
                value={form.values.paymentMethod}
                onChange={(value) => form.setFieldValue("paymentMethod", value)}
                data={[
                  { label: "Thanh toán trực tiếp", value: "DIRECT" },
                  { label: "Thanh toán qua MOMO", value: "MOMO" },
                ]}
                fullWidth
              />
            </Fieldset>
            <Fieldset
              className="grid gap-5"
              legend={
                <span className="text-lg font-medium text-primary-5">
                  Thông tin Thuốc
                </span>
              }
              radius="md"
            >
              <div className="grid gap-4 sm:grid-cols-5">
                {form.values.saleItems.map((item, index) => (
                  <React.Fragment key={index}>
                    <div className="col-span-2">
                      <Select
                        renderOption={renderSelectOption}
                        {...form.getInputProps(`saleItems.${index}.medicineId`)}
                        label="Thuốc"
                        placeholder="Chọn thuốc"
                        data={medicine
                          .filter(
                            (x) =>
                              !form.values.saleItems.some(
                                (item1: any, idx) =>
                                  item1.medicineId == x.id && idx != index
                              )
                          )
                          .map((item) => ({
                            ...item,
                            value: "" + item.id,
                            label: item.name,
                          }))}
                      />
                    </div>
                    <div className="col-span-2">
                      <NumberInput
                        rightSectionWidth={80}
                        rightSection={
                          <div className="text-xs text-white font-medium rounded-md flex gap-1 bg-red-400 p-1">
                            Hàng tồn: {medicineMap[item.medicineId]?.stock}
                          </div>
                        }
                        {...form.getInputProps(`saleItems.${index}.quantity`)}
                        label="Số lượng"
                        max={medicineMap[item.medicineId]?.stock || 0}
                        placeholder="Nhập số lượng"
                        min={0}
                        clampBehavior="strict"
                      />
                    </div>
                    <div className="flex items-end justify-between">
                      {item.quantity && item.medicineId ? (
                        <div className="">
                          Tổng: {item.quantity} X{" "}
                          {medicineMap[item.medicineId]?.unitPrice} ={" "}
                          {item.quantity *
                            medicineMap[item.medicineId]?.unitPrice}
                        </div>
                      ) : (
                        <div></div>
                      )}
                      <ActionIcon
                        size="lg"
                        color="red"
                        onClick={() => form.removeListItem("saleItems", index)}
                      >
                        <IconTrash size={20} />
                      </ActionIcon>
                    </div>
                  </React.Fragment>
                ))}
              </div>
              <div className="flex items-center justify-center">
                <Button
                  onClick={addMore}
                  variant="outline"
                  leftSection={<IconPlus size={17} />}
                >
                  Thêm nữa
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
                Bán thuốc
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
        </div>
      )}
      <Modal opened={opened} size="xl" onClose={close} title="Thuốc đã bán">
        <div className="grid grid-cols-2 gap-5">
          {saleItems?.map((data: any, index: number) => (
            <Card key={index} shadow="md" radius="md" padding="lg" withBorder>
              <Title order={4} mb="sm">
                {medicineMap[data.medicineId]?.name} -{" "}
                {medicineMap[data.medicineId]?.dosage}(
                <span className="text-gray-600">
                  {medicineMap[data.medicineId]?.manufacturer}
                </span>
                )
              </Title>
              <Text size="xs">{data.batchNo}</Text>
              <Divider my="xs" />

              <Grid>
                <Grid.Col span={4}>
                  <Text size="sm" fw={500}>
                    Số lượng:
                  </Text>
                  <Text>{data.quantity}</Text>
                </Grid.Col>

                <Grid.Col span={4}>
                  <Text size="sm" fw={500}>
                    Giá:
                  </Text>
                  <Text>{data.unitPrice}</Text>
                </Grid.Col>

                <Grid.Col span={4}>
                  <Text size="sm" fw={500}>
                    Tổng:
                  </Text>
                  <Text>{data.quantity * data.unitPrice}</Text>
                </Grid.Col>
              </Grid>
            </Card>
          ))}
        </div>
        {saleItems.length === 0 && (
          <Text color="dimmed" size="sm" mt="md">
            Không kê thuốc ở cuộc hẹn này
          </Text>
        )}
      </Modal>
      <Spotlight
        actions={actions}
        nothingFound="Không tìm thấy đơn thuốc nào"
        highlightQuery
        scrollable // <-- THÊM DÒNG NÀY
        maxHeight={500} // <-- THÊM DÒNG NÀY (bạn có thể đổi số 500)
        searchProps={{
          leftSection: <IconSearch size={20} stroke={1.5} />,
          placeholder: "Tìm theo tên bệnh nhân...",
        }}
      />
    </div>
  );
};

export default Sales;
