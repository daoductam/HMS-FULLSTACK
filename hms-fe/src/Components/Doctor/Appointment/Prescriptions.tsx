import {
  ActionIcon,
  Card,
  Divider,
  Grid,
  Modal,
  SegmentedControl,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import {
  IconEye,
  IconLayoutGrid,
  IconMedicineSyrup,
  IconSearch,
  IconTable,
  IconTrash,
} from "@tabler/icons-react";
import { FilterMatchMode } from "primereact/api";
import { Column } from "primereact/column";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import React, { useEffect, useState } from "react";
import { getPrescriptionsByPatientId } from "../../../Service/AppointmentService";
import { formatDate } from "../../../Utility/DateUtility";
import { Navigate, useNavigate } from "react-router-dom";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { Toolbar } from "primereact/toolbar";
import ApCard from "./ApCard";
import PresCard from "./PresCard";

const Prescriptions = ({ appointment }: any) => {
  const [view, setView] = useState("table");
  const [data, setData] = useState<any[]>([]);
  const [opened, { open, close }] = useDisclosure(false);
  const [medicineData, setMedicineData] = useState<any[]>([]);
  const navigate = useNavigate();
  const matches = useMediaQuery("(max-width: 768px)");

  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  useEffect(() => {
    if (!appointment?.patientId) return;
    console.log("appointment in Prescriptions id:", appointment.id);

    console.log("appointment in Prescriptions:", appointment.patientId);
    getPrescriptionsByPatientId(appointment.patientId)
      .then((res) => {
        console.log("appointment in Prescriptions:", res);

        setData(res);
      })
      .catch((err) => {});
  }, [appointment?.patientId]);

  const handleMedicine = (medicine: any) => {
    open();
    setMedicineData(medicine);
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
        <ActionIcon
          onClick={() =>
            navigate("/doctor/appointments/" + rowData.appointmentId)
          }
        >
          <IconEye size={20} stroke={1.5}></IconEye>
        </ActionIcon>
        <ActionIcon
          color="red"
          onClick={() => handleMedicine(rowData.medicines)}
        >
          <IconMedicineSyrup size={20} stroke={1.5}></IconMedicineSyrup>
        </ActionIcon>
      </div>
    );
  };
  return (
    <div>
      <Toolbar className="mb-4 !p-1" end={rightToolbarTemplate}></Toolbar>
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

          <Column
            field="prescriptionDate"
            header="Ngày kê đơn thuốc"
            body={(rowData) => formatDate(rowData.prescriptionDate)}
          />
          <Column
            field="medicine"
            header="Thuốc"
            body={(rowData) => rowData.medicines?.length ?? 0}
            style={{ minWidth: "14rem" }}
          />
          <Column
            field="notes"
            header="Ghi chú"
            style={{ minWidth: "14rem" }}
          />
          <Column
            headerStyle={{ width: "5rem", textAlign: "center" }}
            bodyStyle={{ textAlign: "center", overflow: "visible" }}
            body={actionBodyTemplate}
          />
        </DataTable>
      ) : (
        <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5">
          {data?.map((appointment) => (
            <PresCard
              key={appointment.id}
              {...appointment}
              handleMedicine={handleMedicine}
            />
          ))}
          {data.length === 0 && (
            <div className="col-span-4 text-center text-gray-500">
              Không có đơn thuốc nào được tìm thấy.
            </div>
          )}
        </div>
      )}
      <Modal opened={opened} size="xl" onClose={close} title="Thuốc">
        <div className="grid md:grid-cols-2 grid-cols-1 gap-5">
          {medicineData?.map((data: any, index: number) => (
            <Card key={index} shadow="md" radius="md" padding="lg" withBorder>
              <Title order={4} mb="sm">
                {data.name} ({data.type})
              </Title>

              <Divider my="sm" />

              <Grid>
                <Grid.Col span={6}>
                  <Text size="sm" fw={500}>
                    Liều lượng:
                  </Text>
                  <Text>{data.dosage}</Text>
                </Grid.Col>

                <Grid.Col span={6}>
                  <Text size="sm" fw={500}>
                    Tần suất:
                  </Text>
                  <Text>{data.frequency}</Text>
                </Grid.Col>

                <Grid.Col span={6}>
                  <Text size="sm" fw={500}>
                    Thời gian (ngày):
                  </Text>
                  <Text>{data.duration} ngày</Text>
                </Grid.Col>

                <Grid.Col span={6}>
                  <Text size="sm" fw={500}>
                    Đường dùng:
                  </Text>
                  <Text>{data.route}</Text>
                </Grid.Col>

                <Grid.Col span={6}>
                  <Text size="sm" fw={500}>
                    ID Đơn thuốc:
                  </Text>
                  <Text>{data.prescriptionId}</Text>
                </Grid.Col>

                <Grid.Col span={6}>
                  <Text size="sm" fw={500}>
                    ID Thuốc:
                  </Text>
                  <Text>{data.medicineId ?? "N/A"}</Text>
                </Grid.Col>

                <Grid.Col span={12}>
                  <Text size="sm" fw={500}>
                    Hướng dẫn sử dụng:
                  </Text>
                  <Text>{data.instructions}</Text>
                </Grid.Col>
              </Grid>
            </Card>
          ))}
        </div>
        {medicineData.length === 0 && (
          <Text color="dimmed" size="sm" mt="md">
            Không kê thuốc ở cuộc hẹn này
          </Text>
        )}
      </Modal>
    </div>
  );
};

export default Prescriptions;
