import {
  Avatar,
  Button,
  Divider,
  Group,
  Modal,
  NumberInput,
  Select,
  Table,
  TagsInput,
  TextInput,
} from "@mantine/core";
import { DateInput } from "@mantine/dates";
import { IconEdit } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { bloodGroups } from "../../../Data/DropDownData";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import {
  getPatient,
  updatePatient,
} from "../../../Service/PatientProfileService";
import { formatDate } from "../../../Utility/DateUtility";
import { useForm } from "@mantine/form";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { arrayToCSV } from "../../../Utility/OtherUtility";
import { DropzoneButton } from "../../Utility/Dropzone/DropzoneButton";
import useProtectedImage from "../../Utility/Dropzone/useProtectedImage";
import { useParams, useNavigate } from "react-router-dom"; // <-- THÊM 2 IMPORT NÀY
const AdminEditPatientProfile = () => {
  const { id } = useParams(); // <-- LẤY ID TỪ URL
  const navigate = useNavigate(); // <-- DÙNG ĐỂ QUAY LẠI
  const [editMode, setEdit] = useState(true);
  const [opened, { open, close }] = useDisclosure(false);
  const [profile, setProfile] = useState<any>({});

  useEffect(() => {
    if (id) {
      getPatient(id)
        .then((data) => {
          // <-- SỬA TRONG NÀY
          setProfile({
            ...data,
            allergies: data.allergies ? JSON.parse(data.allergies) : null,
            chronicDisease: data.chronicDisease
              ? JSON.parse(data.chronicDisease)
              : null,
          });
          // TỰ ĐỘNG ĐIỀN FORM SAU KHI TẢI DỮ LIỆU
          form.setValues({
            ...data,
            dob: data.dob ? new Date(data.dob) : undefined,
            chronicDisease: data.chronicDisease
              ? JSON.parse(data.chronicDisease)
              : [],
            allergies: data.allergies ? JSON.parse(data.allergies) : [],
          });
        })
        .catch((error) => console.log(error));
    }
  }, [id]);

  const form = useForm({
    initialValues: {
      dob: "",
      phone: "",
      address: "",
      profilePicture: "",
      cccd: "",
      bloodGroup: "",
      allergies: [],
      chronicDisease: [],
    },

    validate: {
      dob: (value: string) => (value ? null : "Ngày sinh không được để trống"),
      phone: (value: string) => (value ? null : "SĐT không hợp lệ"),
      address: (value: string) =>
        value ? null : "Địa chỉ không được để trống",
      cccd: (value: string) =>
        /^\d{12}$/.test(value) ? null : "CCCD không hợp lệ",
      bloodGroup: (value: string) =>
        value ? null : "Nhóm máu không được để trống",
    },
  });

  //   const handleEdit = () => {
  //     form.setValues({
  //       ...profile,
  //       dob: profile.dob ? new Date(profile.dob) : undefined,
  //       chronicDisease: profile.chronicDisease ?? [],
  //       allergies: profile.allergies ?? [],
  //     });
  //     setEdit(true);
  //   };

  const handleSubmit = (e: any) => {
    let values = form.getValues();
    form.validate();
    if (!form.isValid()) {
      return;
    }

    updatePatient({
      ...profile,
      ...values,
      allergies: values.allergies ? JSON.stringify(values.allergies) : null,
      chronicDisease: values.chronicDisease
        ? JSON.stringify(values.chronicDisease)
        : null,
    })
      .then((data) => {
        successNotification("Cập nhật thông tin thành công!");
        // setProfile({ ...profile, ...values }); // <-- Không cần dòng này
        // setEdit(false); // <-- Không cần dòng này
        navigate("/admin/patients"); // <-- QUAN TRỌNG: Quay về trang Admin
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
            {editMode && (
              <Button variant="filled" size="sm" onClick={open}>
                Upload
              </Button>
            )}
          </div>
          <div className="flex flex-col gap-3">
            <div className="md:text-3xl text-xl font-medium text-neutral-9">
              {profile.name}
            </div>
            <div className="md:text-xl text-lg text-neutral-7">
              {profile.email}
            </div>
          </div>
        </div>
        <Group>
          <Button
            variant="outline"
            size={matches ? "sm" : "lg"}
            onClick={() => navigate("/admin/patients")} // Nút Hủy
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
                CCCD
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <NumberInput
                    {...form.getInputProps("cccd")}
                    maxLength={12}
                    clampBehavior="strict"
                    placeholder="CCCD"
                    hideControls
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.cccd ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Nhóm máu
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <Select
                    {...form.getInputProps("bloodGroup")}
                    placeholder="Nhóm máu"
                    data={bloodGroups}
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {profile.bloodGroup ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Dị ứng
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <TagsInput
                    {...form.getInputProps("allergies")}
                    placeholder="Dị ứng"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {arrayToCSV(profile.allergies) ?? "-"}
                </Table.Td>
              )}
            </Table.Tr>
            <Table.Tr>
              <Table.Td className="md:font-semibold md:text-xl text-lg font-medium">
                Bệnh mãn tính
              </Table.Td>
              {editMode ? (
                <Table.Td className="md:text-xl text-lg">
                  <TagsInput
                    {...form.getInputProps("chronicDisease")}
                    placeholder="Bệnh mãn tính"
                  />
                </Table.Td>
              ) : (
                <Table.Td className="md:text-xl text-lg">
                  {arrayToCSV(profile.chronicDisease) ?? "-"}
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
        title={<span className="text-xl font-medium">Tải Ảnh Đại Diện</span>}
      >
        <DropzoneButton close={close} form={form} id="profilePictureId" />
      </Modal>
    </div>
  );
};

export default AdminEditPatientProfile;
