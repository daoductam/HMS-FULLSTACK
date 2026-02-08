import { Button, PasswordInput, TextInput } from "@mantine/core";
import { IconHeartbeat } from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import { Link } from "react-router-dom";
import { loginUser } from "../Service/UserService";
import {
  errorNotification,
  successNotification,
} from "../Utility/NotificationUtil";
import { useState } from "react";
import { useDispatch } from "react-redux";
import { setJwt } from "../Slices/JwtSlice";
import { jwtDecode } from "jwt-decode";
import { setUser } from "../Slices/UserSlice";

const LoginPage = () => {
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(false);
  const form = useForm({
    initialValues: {
      email: "",
      password: "",
    },

    validate: {
      email: (value: string) =>
        /^\S+@\S+$/.test(value) ? null : "Invalid email",
      password: (value: string) =>
        value.length >= 6 ? null : "Password must be at least 6 characters",
    },
  });

  const handleSubmit = (values: typeof form.values) => {
    setLoading(true);
    loginUser(values)
      .then((_data) => {
        successNotification("Login successful!");
        dispatch(setJwt(_data));
        dispatch(setUser(jwtDecode(_data)));
      })
      .catch((error) => {
        errorNotification(error?.response?.data?.message);
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
      <div className="sm:w-[450px] w-[380px] backdrop-blur-md sm:p-10 p-5  py-8 rounded-lg">
        <form
          onSubmit={form.onSubmit(handleSubmit)}
          className="flex flex-col gap-5 [&_input]:placeholder-neutral-100 [&_.mantine-Input-input]:!border-white focus-within:[&_.mantine-Input-input]:!border-pink-400 [&_.mantine-Input-input]:!border [&_input]:!pl-2 [&_svg]:text-white [&_input]:!text-white"
        >
          <div className="self-center font-medium font-heading text-white text-2xl">
            Login
          </div>
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
          <Button
            loading={loading}
            radius="md"
            size="md"
            type="submit"
            color="pink"
          >
            Đăng Nhập
          </Button>
          <div className="text-pink-400 text-sm self-center">
            Chưa có tài khoản?{" "}
            <Link to="/register" className="hover:underline">
              Đăng Ký
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
