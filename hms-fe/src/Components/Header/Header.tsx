import { ActionIcon, Button } from "@mantine/core";
import {
  IconBellRinging,
  IconLayoutSidebarLeftCollapseFilled,
} from "@tabler/icons-react";
import ProfileMenu from "./ProfileMenu";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { removeJwt } from "../../Slices/JwtSlice";
import { removeUser } from "../../Slices/UserSlice";
import SlideDrawer from "../SlideDrawer/SlideDrawer";
import { useMediaQuery } from "@mantine/hooks";
import { match } from "assert";

const Header = () => {
  const dispatch = useDispatch();
  const jwt = useSelector((state: any) => state.jwt);
  const handleLogout = () => {
    dispatch(removeJwt());
    dispatch(removeUser());
  };
  const matches = useMediaQuery("(max-width: 768px)");
  return (
    <div className="bg-light shadow-lg w-full h-16 flex justify-between px-5 items-center">
      {matches && <SlideDrawer />}
      <div className=""></div>
      <div className="flex gap-5 items-center">
        {jwt ? (
          <Link color="red" onClick={handleLogout} to="dashboard">
            <Button>Thoát</Button>
          </Link>
        ) : (
          <Link to="login">
            <Button>Đăng Nhập</Button>
          </Link>
        )}
        {jwt && (
          <>
            {/* <ActionIcon variant="transparent" size="md" aria-label="Settings">
              <IconBellRinging
                style={{ width: "90%", height: "90%" }}
                stroke={2}
              />
            </ActionIcon> */}
            <ProfileMenu />
          </>
        )}
      </div>
    </div>
  );
};

export default Header;
