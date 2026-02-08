import {
  Avatar,
  Button,
  Divider,
  Modal,
  NumberInput,
  Select,
  Table,
  TagsInput,
  TextInput,
  Group, // <-- Thêm Group
} from "@mantine/core";
import { DateInput } from "@mantine/dates";
// import { IconEdit } from "@tabler/icons-react"; // <-- Không cần nút Edit
import { useEffect, useState } from "react";
// import { useSelector } from "react-redux"; // <-- Không cần lấy user
import {
  doctorDepartments,
  doctorSpecializations,
} from "../../../Data/DropDownData";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { getDoctor, updateDoctor } from "../../../Service/DoctorProfileService";
import { useForm } from "@mantine/form";
import { formatDate } from "../../../Utility/DateUtility";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { DropzoneButton } from "../../Utility/Dropzone/DropzoneButton";
import useProtectedImage from "../../Utility/Dropzone/useProtectedImage";
import { useParams, useNavigate } from "react-router-dom"; // <-- Đã có sẵn

const AdminEditDoctorProfile = () => {
  // SỬA 1: Lấy 'id' từ URL, không dùng 'user'
  const { id } = useParams();
  const navigate = useNavigate();

  // SỬA 2: Bật 'editMode' ngay từ đầu
  const [editMode, setEdit] = useState(true); // <-- Đặt là true
  const [opened, { open, close }] = useDisclosure(false);
  const [profile, setProfile] = useState<any>({});

  useEffect(() => {
    // SỬA 1 (tiếp): Dùng 'id' từ URL để getDoctor
    if (id) {
      getDoctor(id) // <-- Dùng 'id'
        .then((data) => {
          setProfile({
            ...data,
            // (logic parse JSON nếu có)
          });

          // SỬA 3: Tự động điền dữ liệu vào form
          form.setValues({
            ...data,
            dob: data.dob ? new Date(data.dob) : undefined,
          });
        })
        .catch((error) => console.log(error));
    }
  }, [id]); // <-- Phụ thuộc vào 'id'

  const form = useForm({
    initialValues: {
      dob: "",
      phone: "", // API của bạn dùng 'phone' hay 'mobile'?
      address: "",
      licenseNo: "",
      specialization: "",
      department: "", // Select (đơn) không phải là mảng []
      totalExp: 0, // NumberInput nên là số 0, không phải mảng []
    },

    validate: {
      dob: (value: string) => (value ? null : "Ngày sinh không được để trống"),
      phone: (value: string) =>
        /^\d{10}$/.test(value) ? null : "SĐT không hợp lệ",
      address: (value: string) =>
        value ? null : "Địa chỉ không được để trống",
      licenseNo: (value: string) =>
        /^\S{5,}$/.test(value) ? null : "Số giấy phép không hợp lệ",
    },
  });

  // SỬA 2 (tiếp): Xóa hàm 'handleEdit' vì không cần nữa
  // const handleEdit = () => { ... };

  const handleSubmit = (e: any) => {
    let values = form.getValues();
    form.validate();
    if (!form.isValid()) {
      return;
    }

    updateDoctor({
      ...profile, // Gửi ID và các dữ liệu cũ
      ...values, // Gửi các giá trị mới từ form
    })
      .then((_data) => {
        successNotification("Cập nhật thông tin thành công!");

        // SỬA 4: Điều hướng về trang danh sách sau khi lưu
        navigate("/admin/doctors"); // <-- Quay về trang Doctor
      })
      .catch((error) => errorNotification(error?.response?.data?.message));
  };
  const url = useProtectedImage(profile.profilePictureId);
  const matches = useMediaQuery("(max-width: 768px)");

  return (
    <div className="md:p-10 p-5">
      <div className="flex lg:flex-row flex-col justify-between items-center">
        <div className="flex gap-5 items-center">
          <div className="flex flex-col gap-3 items-center">
            <Avatar
              variant="filled"
              src={url}
              size={matches ? 120 : 150}
              alt="it's me"
            ></Avatar>
            {/* Nút Upload vẫn OK vì 'editMode' luôn là true */}
            {editMode && (
              <Button variant="filled" size="sm" onClick={open}>
                Upload
              </Button>
            )}
          </div>
          <div className="flex flex-col gap-3">
            {/* SỬA 1 (tiếp): Hiển thị tên/email của bác sĩ đang sửa */}
            <div className="md:text-3xl text-xl font-medium text-neutral-9">
              {profile.name}
            </div>
            <div className="md:text-xl text-lg text-neutral-7">
              {profile.email}
            </div>
          </div>
        </div>

        {/* SỬA 2 (tiếp): Bỏ nút 'Chỉnh sửa', chỉ còn 'Lưu' và 'Hủy' */}
        <Group>
          <Button
            variant="outline"
            size={matches ? "sm" : "lg"}
            onClick={() => navigate("/admin/doctors")} // Nút Hủy
          >
            Hủy
          </Button>
          <Button
            onClick={handleSubmit}
            variant="filled"
            size={matches ? "sm" : "lg"}
            type="submit"
          >
            Lưu
          </Button>
        </Group>
      </div>
      <Divider my="xl" />
      <div className="">
        {/* ... TOÀN BỘ PHẦN <Table> GIỮ NGUYÊN ... */}
        {/* Nó sẽ tự động hiển thị form vì 'editMode' là true */}

        <div className="text-2xl font-medium mb-5 text-neutral-9">
          Thông tin cá nhân
        </div>
        <Table
          striped
          stripedColor="primary.1"
          verticalSpacing="md"
          withRowBorders={false}
        >
          <Table.Tbody className="[&>tr]:!mb-3 [&_td]:!w1/2">
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Ngày sinh
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <DateInput
                    {...form.getInputProps("dob")}
                    placeholder="Ngày sinh"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {formatDate(profile.dob) ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                SĐT
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <NumberInput
                    {...form.getInputProps("phone")}
                    maxLength={10}
                    clampBehavior="strict"
                    placeholder="SĐT"
                    hideControls
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.phone ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Địa chỉ
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <TextInput
                    {...form.getInputProps("address")}
                    placeholder="Địa chỉ"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.address ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Số Giấy phép
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <TextInput
                    {...form.getInputProps("licenseNo")}
                    placeholder="Số giấy phép"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.licenseNo ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Chuyên môn
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <Select
                    {...form.getInputProps("specialization")}
                    placeholder="Chuyên môn"
                    data={doctorSpecializations}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.specialization ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Khoa
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <Select
                    {...form.getInputProps("department")}
                    placeholder="Khoa"
                    data={doctorDepartments}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.department ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Kinh nghiệm (năm)
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <NumberInput
                    {...form.getInputProps("totalExp")}
                    maxLength={2}
                    max={50}
                    clampBehavior="strict"
                    placeholder="Kinh nghiệm (năm)"
                    hideControls
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.totalExp ?? "-"} {profile.totalExp ? "năm" : ""}
                </Table.Td>
              )}
            </Table.Tr>
          </Table.Tbody>
        </Table>
      </div>
      <Modal
        centered
        opened={opened}
        onClose={close}
        title={<span className="text-xl font-medium">Upload Ảnh Đại Diện</span>}
      >
        <DropzoneButton close={close} form={form} id="profilePictureId" />
      </Modal>
    </div>
  );
};

export default AdminEditDoctorProfile;
