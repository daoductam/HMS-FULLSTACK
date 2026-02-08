import { useEffect, useState } from "react";
import { Table, Button, Group, Text, Paper, Title, Badge } from "@mantine/core";
import { IconCheck, IconX } from "@tabler/icons-react";
import {
  getPendingDoctors,
  approveDoctor,
  rejectDoctor,
} from "../../../Service/UserService";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";

// Định nghĩa kiểu dữ liệu cho User (hoặc import từ file types nếu có)
interface DoctorUser {
  id: number;
  name: string;
  email: string;
  profileId: number;
  status: string;
  createdAt: string;
}

const PendingDoctors = () => {
  const [doctors, setDoctors] = useState<DoctorUser[]>([]);
  const [loading, setLoading] = useState(false);

  // Hàm load dữ liệu
  const fetchData = () => {
    setLoading(true);
    getPendingDoctors()
      .then((res: any) => {
        // Giả sử API trả về mảng trực tiếp hoặc res.data
        setDoctors(res);
      })
      .catch((err) => {
        console.error(err);
        // errorNotification("Không thể tải danh sách bác sĩ chờ duyệt");
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, []);

  // Xử lý Duyệt
  const onApprove = (id: number) => {
    approveDoctor(id)
      .then(() => {
        successNotification("Đã duyệt hồ sơ bác sĩ thành công!");
        // Load lại danh sách để loại bỏ người vừa duyệt
        fetchData();
      })
      .catch((err) => errorNotification(err.response?.data || "Lỗi khi duyệt"));
  };

  // Xử lý Từ chối
  const onReject = (id: number) => {
    if (!window.confirm("Bạn có chắc chắn muốn từ chối hồ sơ này?")) return;

    rejectDoctor(id)
      .then(() => {
        successNotification("Đã từ chối hồ sơ.");
        fetchData();
      })
      .catch((err) =>
        errorNotification(err.response?.data || "Lỗi khi từ chối")
      );
  };

  // Render các dòng trong bảng
  const rows = doctors.map((doc) => (
    <Table.Tr key={doc.id}>
      <Table.Td>{doc.id}</Table.Td>
      <Table.Td className="font-semibold">{doc.name}</Table.Td>
      <Table.Td>{doc.email}</Table.Td>
      <Table.Td>
        <Badge color="orange" variant="light">
          Chờ Xác Nhận
        </Badge>
      </Table.Td>
      <Table.Td>
        <Group gap="sm">
          <Button
            size="xs"
            color="green"
            leftSection={<IconCheck size={14} />}
            onClick={() => onApprove(doc.id)}
          >
            Duyệt
          </Button>
          <Button
            size="xs"
            color="red"
            variant="outline"
            leftSection={<IconX size={14} />}
            onClick={() => onReject(doc.id)}
          >
            Từ chối
          </Button>
        </Group>
      </Table.Td>
    </Table.Tr>
  ));

  return (
    <Paper shadow="xs" p="md" radius="md" className="w-full">
      <Title order={3} mb="md" className="text-gray-700">
        Danh Sách Bác Sĩ Chờ Duyệt
      </Title>

      {doctors.length === 0 && !loading ? (
        <Text c="dimmed" ta="center" py="xl">
          Hiện không có yêu cầu nào đang chờ.
        </Text>
      ) : (
        <Table striped highlightOnHover withTableBorder>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>ID</Table.Th>
              <Table.Th>Họ Tên</Table.Th>
              <Table.Th>Email</Table.Th>
              <Table.Th>Trạng thái</Table.Th>
              <Table.Th>Hành động</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>{rows}</Table.Tbody>
        </Table>
      )}
    </Paper>
  );
};

export default PendingDoctors;
