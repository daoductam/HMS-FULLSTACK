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
} from "@mantine/core";
import { DateInput } from "@mantine/dates";
import { IconEdit } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import {
  bloodGroups,
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

const doctor = {
  dob: "1985-07-20",
  phone: "0987654321",
  address: "123 Nguyễn Huệ, Quận 1, TP.HCM",
  licenseNo: "VN-MED-2024-12345",
  specialization: "Tim mạch",
  department: "Khoa Tim mạch",
  totalExp: 15,
};
const Profile = () => {
  const user = useSelector((state: any) => state.user);
  const [editMode, setEdit] = useState(false);
  const [opened, { open, close }] = useDisclosure(false);
  const [profile, setProfile] = useState<any>({});

  useEffect(() => {
    getDoctor(user.profileId)
      .then((data) =>
        setProfile({
          ...data,
        })
      )
      .catch((error) => console.log(error));
  }, []);

  const form = useForm({
    initialValues: {
      dob: "",
      phone: "",
      address: "",
      licenseNo: "",
      specialization: "",
      department: [],
      totalExp: [],
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

  const handleEdit = () => {
    form.setValues({
      ...profile,
      dob: profile.dob ? new Date(profile.dob) : undefined,
    });
    setEdit(true);
  };

  const handleSubmit = (e: any) => {
    let values = form.getValues();
    form.validate();
    if (!form.isValid()) {
      return;
    }

    updateDoctor({
      ...profile,
      ...values,
    })
      .then((_data) => {
        successNotification("Cập nhật thông tin thành công!");
        setProfile({ ...profile, ...values });
        setEdit(false);
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
              {user.name}
            </div>
            <div className="md:text-xl text-lg text-neutral-7">
              {user.email}
            </div>
          </div>
        </div>
        {!editMode ? (
          <Button
            type="button"
            variant="filled"
            size={matches ? "sm" : "lg"}
            onClick={handleEdit}
            leftSection={<IconEdit />}
          >
            Chỉnh Sửa
          </Button>
        ) : (
          <Button
            onClick={handleSubmit}
            variant="filled"
            size={matches ? "sm" : "lg"}
            type="submit"
          >
            Lưu
          </Button>
        )}
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

export default Profile;
