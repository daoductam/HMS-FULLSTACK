import {
  ActionIcon,
  Badge,
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
  medicineCategories,
  medicineTypes,
  symptoms,
  tests,
} from "../../../Data/DropDownData";
import {
  IconCheck,
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
import { DateInput } from "@mantine/dates";
import {
  addStock,
  getAllStocks,
  updateStock,
} from "../../../Service/InventoryService";
import { Toolbar } from "primereact/toolbar";
import InvCard from "./InvCard";
import { useMediaQuery } from "@mantine/hooks";

const Inventory = () => {
  const [view, setView] = useState("table");
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    batchNo: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    medicineId: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    status: {
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
  const [medicine, setMedicine] = useState<any[]>([]);
  const [edit, setEdit] = useState<Boolean>(false);

  const [loading, setLoading] = useState(false);
  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});
  const matches = useMediaQuery("(max-width: 768px)");

  const form = useForm({
    initialValues: {
      id: null,
      medicineId: "",
      batchNo: "",
      quantity: 0,
      expiryDate: "",
    },

    validate: {
      medicineId: (value: any) => (!value ? "Chưa chọn thuốc" : null),
      batchNo: (value: any) => (!value ? "Chưa nhập mã lô" : null),
      quantity: (value: any) => (value <= 0 ? "Số lượng phải lớn hơn 0" : null),
      expiryDate: (value: any) => (!value ? "Chưa chọn ngày hết hạn" : null),
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
    fetchData();
  }, []);

  const fetchData = () => {
    getAllStocks()
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

  const handleSubmit = (values: any) => {
    let update = false;
    let method;
    if (values.id) {
      update = true;
      method = updateStock;
    } else {
      method = addStock;
    }
    setLoading(true);
    method(values)
      .then((_res) => {
        successNotification(
          `${update ? "Cập nhật" : "Thêm"} Hàng tồn thành công`
        );
        form.reset();
        setEdit(false);
        fetchData();
      })
      .catch((error) => {
        errorNotification(
          error?.response?.data?.message ||
            `${update ? "Cập nhật" : "Thêm"} hàng tồn thất bại`
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
          Thêm Hàng tồn
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
        Thêm Hàng tồn
      </Button>
    );
  };

  const rightToolbarTemplate = () => {
    return (
      <div className="md:flex hidden flex-wrap gap-2 justify-end items-center">
        <SegmentedControl
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
        <ActionIcon onClick={() => onEdit(rowData)}>
          <IconEdit size={20} stroke={1.5}></IconEdit>
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
            {option.manufacturer}
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
              filters={filters}
              filterDisplay="menu"
              globalFilterFields={["medicineId", "batchNo", "status"]}
              emptyMessage="Không cuộc hẹn nào được tìm thấy."
              currentPageReportTemplate="{first} - {last} của {totalRecords} mục"
            >
              {/* <Column
            selectionMode="multiple"
            headerStyle={{ width: "3rem" }}
          ></Column> */}
              <Column
                field="name"
                header="Thuốc"
                sortable
                body={(rowData) => (
                  <span>
                    {medicineMap["" + rowData.medicineId]?.name}{" "}
                    <span className="text-xs text-gray-600">
                      {medicineMap["" + rowData.medicineId]?.manufacturer}
                    </span>
                  </span>
                )}
              />
              <Column field="batchNo" header="Mã lô" />
              <Column field="initialQuantity" header="Số lượng" />
              <Column field="quantity" header="Số lượng còn lại" />
              <Column field="expiryDate" header="Ngày hết hạn" />

              <Column field="status" header="Trạng thái" body={statusBody} />
              <Column
                headerStyle={{ textAlign: "center" }}
                bodyStyle={{ textAlign: "center", overflow: "visible" }}
                body={actionBodyTemplate}
              />
            </DataTable>
          ) : (
            <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5">
              {data?.map((appointment) => (
                <InvCard
                  key={appointment.id}
                  medicineMap={medicineMap}
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
            <Select
              renderOption={renderSelectOption}
              {...form.getInputProps("medicineId")}
              label="Thuốc"
              placeholder="Chọn thuốc"
              data={medicine.map((item) => ({
                ...item,
                value: "" + item.id,
                label: item.name,
              }))}
            />
            <TextInput
              {...form.getInputProps("batchNo")}
              label="Mã lô"
              placeholder="Nhập số mã lô"
              withAsterisk
            />
            <NumberInput
              {...form.getInputProps("quantity")}
              label="Số lượng"
              placeholder="Nhập số lượng"
              min={0}
              clampBehavior="strict"
            />
            <DateInput
              {...form.getInputProps("expiryDate")}
              minDate={new Date()}
              label="Ngày hết hạn"
              placeholder="Nhập ngày hết hạn"
              withAsterisk
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
              {form.values?.id ? "Cập nhật" : "Thêm"} Tồn kho
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

export default Inventory;
