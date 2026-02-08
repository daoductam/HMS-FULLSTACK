import {
  Anchor,
  Badge,
  Breadcrumbs,
  Card,
  Divider,
  Group,
  Tabs,
  Text,
  Title,
} from "@mantine/core";
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getAppointmentDetails } from "../../../Service/AppointmentService";
import { formatDateWithTime } from "../../../Utility/DateUtility";
import {
  IconClipboardHeart,
  IconMessageCircle,
  IconPhoto,
  IconSettings,
  IconStethoscope,
  IconVaccine,
} from "@tabler/icons-react";
import ApReport from "./ApReport";
import Prescriptions from "./Prescriptions";
import { useMediaQuery } from "@mantine/hooks";

const AppointmentDetails = () => {
  const { id } = useParams();
  const [appointment, setAppointment] = useState<any>({});

  useEffect(() => {
    getAppointmentDetails(id)
      .then((res) => {
        setAppointment(res);
      })
      .catch((err) => {
        console.error("Error fetching appointment details: ", err);
      });
  }, [id]);
  return (
    <div>
      <Breadcrumbs mb="md">
        <Link className="text-primary-4 hover:underline" to="/doctor/dashboard">
          Dashboard
        </Link>
        <Link
          className="text-primary-4 hover:underline"
          to="/doctor/appointments"
        >
          Cuộc hẹn
        </Link>
        <Text className="text-primary-4">Chi tiết</Text>
      </Breadcrumbs>
      <div className="">
        <Card shadow="sm" padding="lg" radius="md" withBorder>
          <Group justify="space-between" mb="sm">
            <Title order={2}>{appointment.patientName}</Title>
            <Badge
              color={appointment.status === "CANCELLED" ? "red" : "green"}
              variant="light"
            >
              {appointment.status}
            </Badge>
          </Group>

          <div className="grid md:grid-cols-2 grid-cols-1 md:gap-5 gap-3 mb-2">
            <Text>
              <strong>Email:</strong> {appointment.patientEmail}
            </Text>
            <Text>
              <strong>SĐT:</strong> {appointment.patientPhone}
            </Text>
          </div>
          <div className="grid md:grid-cols-2 grid-cols-1 md:gap-5 gap-3 mb-2">
            <Text>
              <strong>Lý do:</strong> {appointment.reason}
            </Text>
            <Text>
              <strong>Thời gian hẹn:</strong>{" "}
              {formatDateWithTime(appointment.appointmentTime)}
            </Text>
          </div>
          <Text mt="xs">
            <strong>Bác sĩ:</strong> {appointment.doctorName}
          </Text>
          {appointment.notes && (
            <Text mt="sm" color="dimmed" size="sm">
              <strong>Ghi chú:</strong> {appointment.notes}
            </Text>
          )}
        </Card>

        <Tabs variant="pills" my="md" defaultValue="prescriptions">
          <Tabs.List>
            {/* <Tabs.Tab
              value="medical"
              leftSection={<IconStethoscope size={20} />}
            >
              Tiền sử bệnh
            </Tabs.Tab> */}
            <Tabs.Tab
              value="prescriptions"
              leftSection={<IconVaccine size={20} />}
            >
              Đơn thuốc
            </Tabs.Tab>
            <Tabs.Tab
              value="report"
              leftSection={<IconClipboardHeart size={20} />}
            >
              Báo cáo
            </Tabs.Tab>
          </Tabs.List>
          <Divider my="md" />
          <Tabs.Panel value="medical">Medical</Tabs.Panel>

          <Tabs.Panel value="prescriptions">
            <Prescriptions appointment={appointment} />
          </Tabs.Panel>

          <Tabs.Panel value="report">
            <ApReport appointment={appointment} />
          </Tabs.Panel>
        </Tabs>
      </div>
    </div>
  );
};

export default AppointmentDetails;
