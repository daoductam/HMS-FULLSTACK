import React, { useEffect, useState } from "react";
import { getAllDoctorsPaginated } from "../../../Service/DoctorProfileService";
import DoctorCard from "./DoctorCard";
import { Link } from "react-router-dom";
import { TextInput, Pagination, Select, Group, Text } from "@mantine/core";
import { IconSearch } from "@tabler/icons-react";

const Doctor = () => {
  const [doctors, setDoctors] = useState<any[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredDoctors, setFilteredDoctors] = useState<any[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadDoctors = async (page: number, size: number) => {
    setLoading(true);
    try {
      const response = await getAllDoctorsPaginated(page, size);
      console.log(response);
      setDoctors(response.content || []);
      setFilteredDoctors(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDoctors(currentPage, pageSize);
  }, [currentPage, pageSize]);

  // useEffect để lọc (Đã sửa lỗi 'toLowerCase' từ trước)
  useEffect(() => {
    const lowerCaseSearchTerm = searchTerm.toLowerCase();

    const filtered = doctors.filter((doctor) => {
      const nameMatch =
        doctor.name && doctor.name.toLowerCase().includes(lowerCaseSearchTerm);
      const emailMatch =
        doctor.email &&
        doctor.email.toLowerCase().includes(lowerCaseSearchTerm);
      return nameMatch || emailMatch;
    });

    setFilteredDoctors(filtered);
  }, [searchTerm, doctors]);

  return (
    <div>
      <div className="text-xl text-primary-5 font-semibold mb-5">Bác sĩ</div>

      {/* Thanh tìm kiếm và điều khiển phân trang */}
      <Group mb="md" justify="space-between" align="flex-end">
        <TextInput
          placeholder="Tìm kiếm bác sĩ theo tên hoặc email..."
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
        Hiển thị {doctors.length} / {totalElements} bác sĩ
      </Text>

      {/* SỬA LẠI Ở ĐÂY: Dùng lại <div> grid của Tailwind */}
      <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-col-2 grid-cols-1 gap-5 mb-5">
        {/* Dùng filteredDoctors để render */}
        {loading ? (
          <div>Đang tải...</div>
        ) : filteredDoctors.length > 0 ? (
          filteredDoctors.map((doctor: any) => (
            <Link
              to={`/admin/doctor/edit/${doctor.id}`}
              key={doctor.id}
              style={{ textDecoration: "none" }}
            >
              <DoctorCard {...doctor} />
            </Link>
          ))
        ) : (
          <div>Không tìm thấy bác sĩ nào</div>
        )}
      </div>
      {/* KẾT THÚC SỬA */}

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

export default Doctor;
