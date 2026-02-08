import React, { useEffect, useState } from "react";
import { getAllPatientsPaginated } from "../../../Service/PatientProfileService";
import PatientCard from "./PatientCard";
import { TextInput, Pagination, Select, Group, Text } from "@mantine/core";
import { IconSearch } from "@tabler/icons-react";

const Patient = () => {
  const [patients, setPatients] = useState<any[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredPatients, setFilteredPatients] = useState<any[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadPatients = async (page: number, size: number) => {
    setLoading(true);
    try {
      const response = await getAllPatientsPaginated(page, size);
      console.log(response);
      setPatients(response.content || []);
      setFilteredPatients(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPatients(currentPage, pageSize);
  }, [currentPage, pageSize]);

  // useEffect để lọc (Đã sửa lỗi 'toLowerCase' từ trước)
  useEffect(() => {
    const lowerCaseSearchTerm = searchTerm.toLowerCase();

    const filtered = patients.filter((patient) => {
      const nameMatch =
        patient.name &&
        patient.name.toLowerCase().includes(lowerCaseSearchTerm);
      const emailMatch =
        patient.email &&
        patient.email.toLowerCase().includes(lowerCaseSearchTerm);
      return nameMatch || emailMatch;
    });

    setFilteredPatients(filtered);
  }, [searchTerm, patients]);
  return (
    <div>
      <div className="text-xl text-primary-5 font-semibold mb-5">Bệnh nhân</div>

      {/* Thanh tìm kiếm và điều khiển phân trang */}
      <Group mb="md" justify="space-between" align="flex-end">
        <TextInput
          placeholder="Tìm kiếm bệnh nhân theo tên hoặc email..."
          style={{ flex: 1 }}
          leftSection={<IconSearch size={14} />}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.currentTarget.value)}
        />
        <Select
          label="Số lượng mỗi trang"
          value={pageSize.toString()}
          onChange={(value) => {
            setPageSize(Number(value));
            setCurrentPage(0);
          }}
          data={[
            { value: "10", label: "10" },
            { value: "20", label: "20" },
            { value: "50", label: "50" },
            { value: "100", label: "100" },
          ]}
          style={{ width: 150 }}
        />
      </Group>

      {/* Hiển thị thông tin phân trang */}
      <Text size="sm" c="dimmed" mb="md">
        Hiển thị {patients.length} / {totalElements} bệnh nhân
      </Text>

      <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5 mb-5">
        {loading ? (
          <div>Đang tải...</div>
        ) : filteredPatients.length > 0 ? (
          filteredPatients.map((patient: any) => (
            <PatientCard key={patient.id} {...patient} />
          ))
        ) : (
          <div>Không tìm thấy bệnh nhân nào</div>
        )}
      </div>

      {/* Phân trang */}
      {totalPages > 1 && (
        <Pagination
          value={currentPage + 1}
          onChange={(page) => setCurrentPage(page - 1)}
          total={totalPages}
          mt="md"
        />
      )}
    </div>
  );
};

export default Patient;
