import { Button, Container, Title, Text, Group, Stack, Box } from "@mantine/core";
import { IconHeartbeat, IconCalendar, IconUser, IconStethoscope, IconPill } from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { jwtDecode } from "jwt-decode";
import { useEffect } from "react";

const LandingPage = () => {
  const navigate = useNavigate();
  const token = useSelector((state: any) => state.jwt);

  // Nếu đã đăng nhập, redirect đến dashboard
  useEffect(() => {
    if (token) {
      try {
        const user: any = jwtDecode(token);
        navigate(`/${user?.role?.toLowerCase()}/dashboard`, { replace: true });
      } catch (error) {
        // Invalid token, ignore
      }
    }
  }, [token, navigate]);

  const features = [
    {
      icon: IconCalendar,
      title: "Đặt Lịch Dễ Dàng",
      description: "Chọn thời gian và bác sĩ phù hợp với lịch trình của bạn chỉ trong vài cú click",
    },
    {
      icon: IconStethoscope,
      title: "Bác Sĩ Chuyên Nghiệp",
      description: "Đội ngũ bác sĩ giàu kinh nghiệm, tận tâm chăm sóc sức khỏe của bạn",
    },
    {
      icon: IconUser,
      title: "Quản Lý Lịch Hẹn",
      description: "Xem và quản lý tất cả lịch hẹn của bạn ở một nơi, nhận thông báo nhắc nhở",
    },
    {
      icon: IconHeartbeat,
      title: "Tiết Kiệm Thời Gian",
      description: "Không cần chờ đợi lâu, đặt lịch trước giúp bạn tiết kiệm thời gian quý báu",
    },
  ];

  return (
    <div
      style={{
        background: 'url("/bg.jpg")',
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        minHeight: "100vh",
        position: "relative",
      }}
    >
      {/* Overlay để làm tối background một chút */}
      <div
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: "rgba(0, 0, 0, 0.4)",
          zIndex: 1,
        }}
      />

      {/* Content */}
      <Container
        size="xl"
        style={{
          position: "relative",
          zIndex: 2,
          paddingTop: "80px",
          paddingBottom: "80px",
        }}
      >
        {/* Header với Logo */}
        <Group justify="space-between" mb={60}>
          <Group gap="xs">
            <IconHeartbeat size={50} stroke={2.5} color="#ec4899" />
            <Title order={1} c="pink" fw={700} size={40}>
              TPVK
            </Title>
          </Group>
          <Group gap="md">
            <Button
              variant="subtle"
              color="pink"
              size="md"
              onClick={() => navigate("/login")}
              style={{ color: "white" }}
            >
              Đăng Nhập
            </Button>
            <Button
              color="pink"
              size="md"
              onClick={() => navigate("/register")}
            >
              Đăng Ký
            </Button>
          </Group>
        </Group>

        {/* Hero Section */}
        <Stack align="center" gap="xl" mb={80} style={{ textAlign: "center" }}>
          <Title
            order={1}
            c="white"
            fw={800}
            size={60}
            style={{ lineHeight: 1.2 }}
          >
            Đặt Lịch Khám Bệnh
            <br />
            <Text span c="pink" inherit>
              Nhanh Chóng & Tiện Lợi
            </Text>
          </Title>
          <Text c="white" size="xl" maw={700} style={{ opacity: 0.9 }}>
            Đặt lịch khám bệnh trực tuyến tại phòng khám của chúng tôi một cách dễ dàng. 
            Chọn bác sĩ, thời gian phù hợp và nhận xác nhận ngay lập tức. 
            Tiết kiệm thời gian, chăm sóc sức khỏe hiệu quả.
          </Text>
          <Group gap="md" mt="md">
            <Button
              color="pink"
              size="lg"
              radius="md"
              onClick={() => navigate("/register")}
            >
              Đặt Lịch Ngay
            </Button>
            <Button
              variant="outline"
              color="pink"
              size="lg"
              radius="md"
              onClick={() => navigate("/login")}
              style={{
                borderColor: "white",
                color: "white",
              }}
            >
              Đăng Nhập
            </Button>
          </Group>
        </Stack>

        {/* Features Section */}
        <Box
          style={{
            backgroundColor: "rgba(255, 255, 255, 0.1)",
            backdropFilter: "blur(10px)",
            borderRadius: "20px",
            padding: "60px 40px",
            border: "1px solid rgba(255, 255, 255, 0.2)",
          }}
        >
          <Title order={2} c="white" ta="center" mb={40} fw={700}>
            Tại Sao Chọn Chúng Tôi?
          </Title>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
              gap: "30px",
            }}
          >
            {features.map((feature, index) => (
              <Stack
                key={index}
                align="center"
                gap="md"
                style={{
                  padding: "30px",
                  backgroundColor: "rgba(255, 255, 255, 0.05)",
                  borderRadius: "15px",
                  border: "1px solid rgba(255, 255, 255, 0.1)",
                  transition: "transform 0.3s ease",
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = "translateY(-5px)";
                  e.currentTarget.style.backgroundColor = "rgba(255, 255, 255, 0.1)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "translateY(0)";
                  e.currentTarget.style.backgroundColor = "rgba(255, 255, 255, 0.05)";
                }}
              >
                <Box
                  style={{
                    padding: "20px",
                    borderRadius: "50%",
                    backgroundColor: "rgba(236, 72, 153, 0.2)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <feature.icon size={40} color="#ec4899" stroke={2} />
                </Box>
                <Title order={3} c="white" ta="center" size="h4" fw={600}>
                  {feature.title}
                </Title>
                <Text c="white" ta="center" size="sm" style={{ opacity: 0.8 }}>
                  {feature.description}
                </Text>
              </Stack>
            ))}
          </div>
        </Box>

        {/* Footer CTA */}
        <Stack align="center" gap="md" mt={60}>
          <Text c="white" size="lg" ta="center" style={{ opacity: 0.9 }}>
            Sẵn sàng đặt lịch khám bệnh ngay hôm nay?
          </Text>
          <Button
            color="pink"
            size="lg"
            radius="md"
            onClick={() => navigate("/register")}
          >
            Đặt Lịch Ngay
          </Button>
        </Stack>
      </Container>
    </div>
  );
};

export default LandingPage;

