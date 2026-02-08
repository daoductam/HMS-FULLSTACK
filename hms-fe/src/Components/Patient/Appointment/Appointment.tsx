import React, { useState, useEffect } from "react";
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { Column } from "primereact/column";
import { Tag } from "primereact/tag";
import {
  ActionIcon,
  Button,
  LoadingOverlay,
  Modal,
  SegmentedControl,
  Select,
  Text,
  Textarea,
  TextInput,
} from "@mantine/core";
import {
  IconEdit,
  IconLayoutGrid,
  IconPlus,
  IconSearch,
  IconTable,
  IconTrash,
} from "@tabler/icons-react";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { getDoctorDropdowns } from "../../../Service/DoctorProfileService";
import { DateTimePicker } from "@mantine/dates";
import { useForm } from "@mantine/form";
import { appointmentReasons } from "../../../Data/DropDownData";
import { useSelector } from "react-redux";
import {
  cancelAppointment,
  getAppointmentsByPatient,
  scheduleAppointment,
} from "../../../Service/AppointmentService";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { formatDateWithTime, formatDateTimeToLocalString } from "../../../Utility/DateUtility";
import { modals } from "@mantine/modals";
import "primereact/resources/themes/lara-light-blue/theme.css";
import { Toolbar } from "primereact/toolbar";
import ApCard from "./ApCard";

interface Country {
  name: string;
  code: string;
}

interface Representative {
  name: string;
  image: string;
}

interface Customer {
  id: number;
  name: string;
  country: Country;
  company: string;
  date: string | Date;
  status: string;
  verified: boolean;
  activity: number;
  representative: Representative;
  balance: number;
}

const Appointment = () => {
  const [view, setView] = useState("table");
  const [opened, { open, close }] = useDisclosure(false);
  const [doctors, setDoctors] = useState<any[]>([]);
  const [tab, setTab] = useState("Hôm nay");
  const [appointments, setAppointments] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const user = useSelector((state: any) => state.user);
  const [selectedCustomers, setSelectedCustomers] = useState<Customer[]>([]);
  const matches = useMediaQuery("(max-width: 768px)");

  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    doctorName: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    reason: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    notes: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    status: { value: null, matchMode: FilterMatchMode.IN },
  });
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");

  const getSeverity = (status: string) => {
    switch (status) {
      case "CANCELLED":
        return "danger";

      case "COMPLETED":
        return "success";

      case "SCHEDULED":
        return "info";

      case "negotiation":
        return "warning";

      default:
        return null;
    }
  };

  useEffect(() => {
    fetchData();
    getDoctorDropdowns()
      .then((data) => {
        setDoctors(
          data.map((doctor: any) => ({
            value: "" + doctor.id,
            label: doctor.name,
          }))
        );
      })
      .catch((error) => {
        console.error("Error fetching doctor dropdowns:", error);
      });
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const fetchData = () => {
    getAppointmentsByPatient(user.profileId)
      .then((data) => {
        setAppointments(getCustomers(data));
      })
      .catch((error) => {
        console.error("Error fetching appointments by patient:", error);
      });
  };
  const getCustomers = (data: Customer[]) => {
    return [...(data || [])].map((d) => {
      d.date = new Date(d.date);

      return d;
    });
  };

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const form = useForm({
    initialValues: {
      doctorId: "",
      patientId: user.profileId,
      appointmentTime: new Date(),
      reason: "",
      notes: "",
    },

    validate: {
      doctorId: (value: string) =>
        value ? null : "Bác sĩ không được để trống",
      appointmentTime: (value: Date) =>
        value ? null : "Thời gian không được để trống",
      reason: (value: string) => (value ? null : "Lý do không được để trống"),
      notes: (value: string) => null,
    },
  });
  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between items-center">
        <Button leftSection={<IconPlus />} onClick={open} variant="filled">
          Tạo Lịch Hẹn
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

  const statusBodyTemplate = (rowData: Customer) => {
    return (
      <Tag value={rowData.status} severity={getSeverity(rowData.status)} />
    );
  };

  const handleDelete = (rowData: any) => {
    modals.openConfirmModal({
      title: <span className="text-xl font-semibold">Chắc chắn không?</span>,
      centered: true,
      children: (
        <Text size="sm">
          Bạn muốn hủy cuộc hẹn này? Hành động này không thể hoàn tác.
        </Text>
      ),
      labels: { confirm: "Confirm", cancel: "Cancel" },
      onConfirm: () => {
        cancelAppointment(rowData.id)
          .then(() => {
            successNotification("Hủy cuộc hẹn thành công!");
            setAppointments(
              appointments.map((appointment) =>
                appointment.id === rowData.id
                  ? { ...appointment, status: "CANCELLED" }
                  : appointment
              )
            );
          })
          .catch((error) => {
            errorNotification(error?.response?.data?.message);
          });
      },
    });
  };

  const actionBodyTemplate = (rowData: any) => {
    return (
      <div className="flex gap-2 ">
        {/* <ActionIcon>
          <IconEdit size={20} stroke={1.5}></IconEdit>
        </ActionIcon> */}
        <ActionIcon color="red" onClick={() => handleDelete(rowData)}>
          <IconTrash size={20} stroke={1.5}></IconTrash>
        </ActionIcon>
      </div>
    );
  };

  const header = renderHeader();
  const handleSubmit = (values: any) => {
    setLoading(true);

    const payload = {
      ...values,
      // Format datetime theo local timezone (không convert sang UTC)
      // Format: YYYY-MM-DDTHH:mm:ss (local time)
      appointmentTime: formatDateTimeToLocalString(new Date(values.appointmentTime)),
    };
    scheduleAppointment(payload)
      .then((_data) => {
        close();
        form.reset();
        fetchData();
        successNotification("Đặt lịch hẹn thành công!");
      })
      .catch((error) => {
        errorNotification(error?.response?.data?.message);
      })
      .finally(() => setLoading(false));
  };
  const timeTemplate = (rowData: any) => {
    return <span>{formatDateWithTime(rowData.appointmentTime)}</span>;
  };

  const leftToolbarTemplate = () => {
    return (
      <Button
        leftSection={<IconPlus />}
        size={matches ? "xs" : "md"}
        onClick={open}
        variant="filled"
      >
        Đặt lịch
      </Button>
    );
  };

  const rightToolbarTemplate = () => {
    return (
      <div className="md:flex hidden gap-5 items-center">
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
          leftSection={<IconSearch />}
          fw={500}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Từ khóa tìm kiếm"
        />
      </div>
    );
  };

  const centerToolBarTemplate = () => {
    return (
      <SegmentedControl
        value={tab}
        size={matches ? "xs" : "md"}
        onChange={setTab}
        variant="filled"
        color={tab === "Hôm nay" ? "blue" : tab === "Sắp tới" ? "green" : "red"}
        data={["Hôm nay", "Sắp tới", "Đã qua"]}
      />
    );
  };

  const filteredAppointments = appointments.filter((appointment) => {
    const appointmentDate = new Date(appointment.appointmentTime);
    const today = new Date();

    if (tab === "Hôm nay") {
      return (
        appointmentDate.getFullYear() === today.getFullYear() &&
        appointmentDate.getMonth() === today.getMonth() &&
        appointmentDate.getDate() === today.getDate()
      );
    } else if (tab === "Sắp tới") {
      return appointmentDate > today;
    } else if (tab === "Đã qua") {
      return appointmentDate < today;
    }
    return true;
  });

  return (
    <div className="card">
      <Toolbar
        className="mb-4 md:p-3 p-1"
        start={leftToolbarTemplate}
        center={centerToolBarTemplate}
        end={rightToolbarTemplate}
      ></Toolbar>
      {view == "table" && !matches ? (
        <DataTable
          stripedRows
          value={filteredAppointments}
          size="small"
          paginator
          rows={10}
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          rowsPerPageOptions={[10, 25, 50]}
          dataKey="id"
          filters={filters}
          filterDisplay="menu"
          globalFilterFields={["doctorName", "reason", "notes", "status"]}
          emptyMessage="Không cuộc hẹn nào được tìm thấy."
          currentPageReportTemplate="{first} - {last} của {totalRecords} cuộc hẹn"
        >
          <Column
            selectionMode="multiple"
            headerStyle={{ width: "3rem" }}
          ></Column>
          <Column
            field="doctorName"
            header="Bác sĩ"
            sortable
            filter
            filterPlaceholder="Tìm theo tên"
            style={{ minWidth: "14rem" }}
          />
          <Column
            field="appointmentTime"
            header="Thời gian hẹn"
            sortable
            style={{ minWidth: "14rem" }}
            body={timeTemplate}
          />
          <Column
            field="reason"
            header="Lý do"
            sortable
            filter
            filterPlaceholder="Tìm theo tên"
            style={{ minWidth: "14rem" }}
          />
          <Column
            field="notes"
            header="Ghi chú"
            sortable
            filter
            filterPlaceholder="Tìm theo tên"
            style={{ minWidth: "14rem" }}
          />
          <Column
            field="status"
            header="Trạng thái"
            sortable
            filterMenuStyle={{ width: "14rem" }}
            style={{ minWidth: "12rem" }}
            body={statusBodyTemplate}
            filter
          />
          <Column
            headerStyle={{ width: "5rem", textAlign: "center" }}
            bodyStyle={{ textAlign: "center", overflow: "visible" }}
            body={actionBodyTemplate}
          />
        </DataTable>
      ) : (
        <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5">
          {filteredAppointments?.map((appointment) => (
            <ApCard key={appointment.id} {...appointment} />
          ))}
          {filteredAppointments.length === 0 && (
            <div className="col-span-4 text-center text-gray-500">
              Không có cuộc hẹn nào được tìm thấy.
            </div>
          )}
        </div>
      )}
      <Modal
        size="lg"
        opened={opened}
        onClose={close}
        title={<div className="text-xl font-semibold text-primary-5">Đặt</div>}
        centered
      >
        <LoadingOverlay
          visible={loading}
          zIndex={1000}
          overlayProps={{ radius: "sm", blur: 2 }}
        />
        <form
          onSubmit={form.onSubmit(handleSubmit)}
          className="grid grid-cols-1  gap-5"
        >
          <Select
            {...form.getInputProps("doctorId")}
            withAsterisk
            data={doctors}
            label="Bác sĩ"
            placeholder="Chọn Bác sĩ"
          />
          <DateTimePicker
            minDate={new Date()}
            {...form.getInputProps("appointmentTime")}
            withAsterisk
            label="Thời gian hẹn"
            placeholder="Chọn ngày và giờ"
          />
          <Select
            {...form.getInputProps("reason")}
            data={appointmentReasons}
            withAsterisk
            label="Lý do hẹn"
            placeholder="Nhập lý do hẹn"
          />
          <Textarea
            {...form.getInputProps("notes")}
            label="Ghi chú(Tùy chọn)"
            placeholder="Nhập ghi chú"
          />
          <Button type="submit" variant="filled" fullWidth>
            Đặt
          </Button>
        </form>
      </Modal>
    </div>
  );
};

export default Appointment;
