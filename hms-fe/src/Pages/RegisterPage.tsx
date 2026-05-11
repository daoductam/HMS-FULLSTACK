import {
  Button,
  PasswordInput,
  SegmentedControl,
  TextInput,
} from "@mantine/core";
import { IconHeartbeat } from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../Service/UserService";
import {
  errorNotification,
  successNotification,
} from "../Utility/NotificationUtil";
import { useState } from "react";

const RegisterPage = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const form = useForm<{
    name: string;
    role: string;
    email: string;
    password: string;
    confirmPassword: string;
  }>({
    initialValues: {
      name: "",
      role: "PATIENT",
      email: "",
      password: "",
      confirmPassword: "",
    },

    validate: {
      name: (value: string) => (value.trim() ? null : "Name is required"),
      email: (value: string) =>
        /^\S+@\S+$/.test(value) ? null : "Invalid email",
      password: (value: string) =>
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(value) 
          ? null : "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt",
      confirmPassword: (value: string, values) =>
        value === values.password ? null : "Passwords do not match",
    },
  });

  const handleSubmit = (values: typeof form.values) => {
    setLoading(true);
    // Loại bỏ confirmPassword trước khi gửi lên Backend
    const { confirmPassword, ...registerData } = values;
    
    registerUser(registerData)
      .then((data) => {
        // KIỂM TRA ROLE TẠI ĐÂY
        if (values.role === "DOCTOR") {
          // Thông báo riêng cho Bác sĩ
          successNotification(
            "Đăng ký thành công! Hồ sơ bác sĩ của bạn đang được xét duyệt. Vui lòng kiểm tra email để biết kết quả."
          );
          // Tùy chọn: Reset form nhưng không chuyển trang ngay, hoặc chuyển về trang chủ thay vì trang login
          form.reset();
          // navigate("/"); // Nếu muốn về trang chủ
        } else {
          // Logic cũ cho Bệnh nhân
          successNotification("Registration successful!");
          navigate("/login");
        }
      })
      .catch((error) => {
        // Xử lý lỗi an toàn hơn
        const msg = error?.response?.data?.message || "Something went wrong";
        errorNotification(msg);
      })
      .finally(() => setLoading(false));
  };

  return (
    <div
      style={{ background: 'url("/bg.jpg")' }}
      className="h-screen w-screen !bg-cover !bg-center !bg-no-repeat flex flex-col items-center justify-center"
    >
      <div className=" py-3 text-pink-500 flex  gap-1 items-center ">
        <IconHeartbeat size={45} stroke={2.5} />
        <span className="font-heading font-semibold text-4xl">TPVK</span>
      </div>
      <div className="w-[450px] backdrop-blur-md p-10 py-8 rounded-lg">
        <form
          onSubmit={form.onSubmit(handleSubmit)}
          className="flex flex-col gap-5 [&_input]:placeholder-neutral-100 [&_.mantine-Input-input]:!border-white focus-within:[&_.mantine-Input-input]:!border-pink-400 [&_.mantine-Input-input]:!border [&_input]:!pl-2 [&_svg]:text-white [&_input]:!text-white"
        >
          <div className="self-center font-medium font-heading text-white text-2xl">
            Đăng Ký
          </div>
          <SegmentedControl
            {...form.getInputProps("role")}
            fullWidth
            size="md"
            radius="md"
            color="pink"
            bg="none"
            className="[&_*]:text-white border border-white"
            data={[
              { label: "Patient", value: "PATIENT" },
              { label: "Doctor", value: "DOCTOR" },
              // { label: "Admin", value: "ADMIN" },
            ]}
          />

          <TextInput
            {...form.getInputProps("name")}
            className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Name"
          />
          <TextInput
            {...form.getInputProps("email")}
            className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Email"
          />
          <PasswordInput
            {...form.getInputProps("password")}
            className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Password"
          />
          <PasswordInput
            {...form.getInputProps("confirmPassword")}
            className="transition duration-300"
            variant="unstyled"
            size="md"
            radius="md"
            placeholder="Confirm Password"
          />
          <Button
            loading={loading}
            radius="md"
            size="md"
            type="submit"
            color="pink"
          >
            Đăng Ký
          </Button>
          <div className="text-pink-400 text-sm self-center">
            Đã có tài khoản?{" "}
            <Link to="/login" className="hover:underline">
              Đăng Nhập
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
