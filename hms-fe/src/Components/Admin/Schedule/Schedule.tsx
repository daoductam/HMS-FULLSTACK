import {
  Button,
  Card,
  Fieldset,
  Group,
  Loader,
  LoadingOverlay,
  Modal,
  NumberInput,
  Select,
  Table,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { DatePicker } from "@mantine/dates";
import { useForm } from "@mantine/form";
import { useDisclosure } from "@mantine/hooks";
import { IconCalendar, IconLock, IconLockOpen, IconPlus } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import {
  createSchedule,
  getAllShifts,
  getSchedulesByDoctor,
  lockSchedule,
  unlockSchedule,
} from "../../../Service/AppointmentService";
import { getAllDoctors } from "../../../Service/DoctorProfileService";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { formatDate } from "../../../Utility/DateUtility";

const Schedule = () => {
  const [doctors, setDoctors] = useState<any[]>([]);
  const [shifts, setShifts] = useState<any[]>([]);
  const [schedules, setSchedules] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [opened, { open, close }] = useDisclosure(false);
  const [lockModalOpened, { open: openLockModal, close: closeLockModal }] = useDisclosure(false);
  const [selectedSchedule, setSelectedSchedule] = useState<any>(null);
  const [selectedDoctorId, setSelectedDoctorId] = useState<string>("");

  const form = useForm({
    initialValues: {
      doctorId: "",
      scheduleDates: [] as Date[],
      shifts: [] as Array<{ shiftId: string; maxSlots: number }>,
    },
  });

  const lockForm = useForm({
    initialValues: {
      reason: "",
    },
  });

  useEffect(() => {
    fetchDoctors();
    fetchShifts();
  }, []);

  const fetchDoctors = async () => {
    try {
      const data = await getAllDoctors();
      setDoctors(data);
    } catch (error) {
      console.error("Error fetching doctors:", error);
    }
  };

  const fetchShifts = async () => {
    try {
      const data = await getAllShifts();
      setShifts(data);
      // Khởi tạo form với tất cả shifts nếu chưa có
      if (form.values.shifts.length === 0) {
        form.setFieldValue(
          "shifts",
          data.map((shift: any) => ({ shiftId: String(shift.id), maxSlots: 20 }))
        );
      }
    } catch (error) {
      console.error("Error fetching shifts:", error);
    }
  };

  const fetchSchedules = async (doctorId: string) => {
    if (!doctorId) return;
    setLoading(true);
    try {
      const startDate = new Date();
      const endDate = new Date();
      endDate.setDate(endDate.getDate() + 30);
      const data = await getSchedulesByDoctor(
        doctorId,
        startDate.toISOString().split("T")[0],
        endDate.toISOString().split("T")[0]
      );
      setSchedules(data);
    } catch (error) {
      console.error("Error fetching schedules:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateSchedule = async (values: any) => {
    setLoading(true);
    try {
      // Validate dates
      if (!values.scheduleDates || values.scheduleDates.length === 0) {
        errorNotification("Vui lòng chọn ít nhất một ngày làm việc");
        setLoading(false);
        return;
      }

      // Validate shifts
      const validShifts = values.shifts.filter((s: any) => s.shiftId);
      if (validShifts.length === 0) {
        errorNotification("Vui lòng chọn ít nhất một ca làm việc");
        setLoading(false);
        return;
      }

      const shiftsData = validShifts.map((s: any) => ({
        shiftId: Number(s.shiftId),
        maxSlots: Number(s.maxSlots) || 20,
      }));

      // Tạo lịch cho từng ngày
      const savedDoctorId = values.doctorId;
      let successCount = 0;
      let errorCount = 0;

      for (const date of values.scheduleDates) {
        try {
          const scheduleDate = date instanceof Date 
            ? date.toISOString().split("T")[0]
            : date;

          const requestData = {
            doctorId: Number(values.doctorId),
            scheduleDate: scheduleDate,
            shifts: shiftsData,
          };

          await createSchedule(requestData);
          successCount++;
        } catch (error: any) {
          console.error(`Error creating schedule for ${date}:`, error);
          errorCount++;
        }
      }

      if (successCount > 0) {
        successNotification(`Đã tạo lịch làm việc thành công cho ${successCount} ngày`);
      }
      if (errorCount > 0) {
        errorNotification(`Không thể tạo lịch cho ${errorCount} ngày (có thể đã tồn tại)`);
      }

      // Reset form nhưng giữ lại doctorId và shifts
      form.setFieldValue("scheduleDates", []);
      form.setFieldValue("doctorId", savedDoctorId);
      // Giữ lại shifts để không phải chọn lại
      close();
      
      // Fetch lại danh sách lịch
      if (savedDoctorId) {
        await fetchSchedules(savedDoctorId);
      }
      if (selectedDoctorId) {
        await fetchSchedules(selectedDoctorId);
      }
    } catch (error: any) {
      console.error("Error creating schedule:", error);
      const errorMessage = error?.response?.data?.message 
        || error?.message 
        || "Không thể tạo lịch làm việc";
      errorNotification(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleLockSchedule = async (schedule: any) => {
    setSelectedSchedule(schedule);
    openLockModal();
  };

  const handleConfirmLock = async () => {
    if (!selectedSchedule) return;
    setLoading(true);
    try {
      await lockSchedule({
        doctorId: selectedSchedule.doctorId,
        scheduleDate: selectedSchedule.scheduleDate,
        reason: lockForm.values.reason,
      });
      successNotification("Đã khóa lịch làm việc");
      closeLockModal();
      lockForm.reset();
      if (selectedDoctorId) {
        fetchSchedules(selectedDoctorId);
      }
    } catch (error: any) {
      errorNotification(
        error?.response?.data?.message || "Không thể khóa lịch làm việc"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleUnlockSchedule = async (schedule: any) => {
    setLoading(true);
    try {
      await unlockSchedule(
        schedule.doctorId,
        schedule.scheduleDate
      );
      successNotification("Đã mở khóa lịch làm việc");
      if (selectedDoctorId) {
        fetchSchedules(selectedDoctorId);
      }
    } catch (error: any) {
      errorNotification(
        error?.response?.data?.message || "Không thể mở khóa lịch làm việc"
      );
    } finally {
      setLoading(false);
    }
  };

  const handleDoctorChange = (doctorId: string) => {
    setSelectedDoctorId(doctorId);
    form.setFieldValue("doctorId", doctorId);
    if (doctorId) {
      fetchSchedules(doctorId);
    } else {
      setSchedules([]);
    }
  };

  // Khởi tạo shifts khi mở modal nếu chưa có
  const handleOpenModal = () => {
    // Đảm bảo shifts được khởi tạo
    if (form.values.shifts.length === 0 && shifts.length > 0) {
      form.setFieldValue(
        "shifts",
        shifts.map((shift: any) => ({ shiftId: String(shift.id), maxSlots: 20 }))
      );
    }
    open();
  };

  return (
    <div>
      <div className="mb-5 flex items-center justify-between">
        <Title order={2}>Quản lý Lịch làm việc & Ca trực</Title>
        <Button leftSection={<IconPlus />} onClick={handleOpenModal}>
          Tạo Lịch Làm Việc
        </Button>
      </div>

      <Card withBorder p="md" radius="md" mb="md">
        <Select
          label="Chọn Bác sĩ"
          placeholder="Chọn bác sĩ để xem lịch làm việc"
          data={doctors.map((d) => ({
            value: String(d.id),
            label: d.name,
          }))}
          value={selectedDoctorId}
          onChange={(value) => handleDoctorChange(value || "")}
        />
      </Card>

      <LoadingOverlay visible={loading} />
      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Ngày</Table.Th>
            <Table.Th>Trạng thái</Table.Th>
            <Table.Th>Ca Sáng</Table.Th>
            <Table.Th>Ca Chiều</Table.Th>
            <Table.Th>Thao tác</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {schedules.length === 0 ? (
            <Table.Tr>
              <Table.Td colSpan={5} ta="center">
                <Text c="dimmed">Chưa có lịch làm việc</Text>
              </Table.Td>
            </Table.Tr>
          ) : (
            schedules.map((schedule) => (
              <Table.Tr key={schedule.id}>
                <Table.Td>{formatDate(schedule.scheduleDate)}</Table.Td>
                <Table.Td>
                  {schedule.isLocked ? (
                    <Text c="red" fw={500}>
                      Đã khóa
                    </Text>
                  ) : (
                    <Text c="green" fw={500}>
                      Hoạt động
                    </Text>
                  )}
                </Table.Td>
                <Table.Td>
                  {schedule.shifts
                    ?.filter((s: any) => s.shift?.name === "MORNING")
                    .map((s: any) => (
                      <Text key={s.id} size="sm">
                        {s.bookedSlots}/{s.maxSlots} (Còn: {s.availableSlots})
                      </Text>
                    ))}
                </Table.Td>
                <Table.Td>
                  {schedule.shifts
                    ?.filter((s: any) => s.shift?.name === "AFTERNOON")
                    .map((s: any) => (
                      <Text key={s.id} size="sm">
                        {s.bookedSlots}/{s.maxSlots} (Còn: {s.availableSlots})
                      </Text>
                    ))}
                </Table.Td>
                <Table.Td>
                  <Group gap="xs">
                    {schedule.isLocked ? (
                      <Button
                        size="xs"
                        variant="light"
                        color="green"
                        leftSection={<IconLockOpen size={16} />}
                        onClick={() => handleUnlockSchedule(schedule)}
                      >
                        Mở khóa
                      </Button>
                    ) : (
                      <Button
                        size="xs"
                        variant="light"
                        color="red"
                        leftSection={<IconLock size={16} />}
                        onClick={() => handleLockSchedule(schedule)}
                      >
                        Khóa
                      </Button>
                    )}
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))
          )}
        </Table.Tbody>
      </Table>

      {/* Modal tạo lịch */}
      <Modal opened={opened} onClose={close} title="Tạo Lịch Làm Việc" size="lg">
        <form onSubmit={form.onSubmit(handleCreateSchedule)}>
          <Fieldset legend="Thông tin lịch làm việc" mb="md">
            <Select
              label="Bác sĩ"
              placeholder="Chọn bác sĩ"
              data={doctors.map((d) => ({
                value: String(d.id),
                label: d.name,
              }))}
              required
              {...form.getInputProps("doctorId")}
            />
            <div>
              <Text size="sm" fw={500} mb={5}>
                Ngày làm việc <span style={{ color: "red" }}>*</span>
              </Text>
              <DatePicker
                minDate={new Date()}
                value={null}
                onChange={(value: string | Date | null) => {
                  if (value) {
                    // Convert to Date nếu là string
                    const date = value instanceof Date ? value : new Date(value);
                    // Kiểm tra xem ngày đã được chọn chưa
                    const dateStr = date.toISOString().split("T")[0];
                    const existingDates = form.values.scheduleDates;
                    const isAlreadySelected = existingDates.some(
                      (d) => d.toISOString().split("T")[0] === dateStr
                    );

                    if (isAlreadySelected) {
                      // Nếu đã chọn, xóa khỏi danh sách
                      const newDates = existingDates.filter(
                        (d) => d.toISOString().split("T")[0] !== dateStr
                      );
                      form.setFieldValue("scheduleDates", newDates);
                    } else {
                      // Nếu chưa chọn, thêm vào danh sách
                      form.setFieldValue("scheduleDates", [...existingDates, date]);
                    }
                  }
                }}
                getDayProps={(date: string) => {
                  const dateStr = date;
                  const isSelected = form.values.scheduleDates.some(
                    (d) => d.toISOString().split("T")[0] === dateStr
                  );
                  return {
                    style: isSelected
                      ? { backgroundColor: "var(--mantine-color-blue-5)", color: "white" }
                      : {},
                  };
                }}
              />
              {form.values.scheduleDates.length > 0 && (
                <div style={{ marginTop: 10 }}>
                  <Text size="xs" fw={500} mb={5}>
                    Đã chọn ({form.values.scheduleDates.length} ngày):
                  </Text>
                  <Group gap="xs" wrap="wrap">
                    {form.values.scheduleDates.map((date, index) => (
                      <Button
                        key={index}
                        size="xs"
                        variant="light"
                        color="blue"
                        onClick={() => {
                          const newDates = form.values.scheduleDates.filter((_, i) => i !== index);
                          form.setFieldValue("scheduleDates", newDates);
                        }}
                      >
                        {date.toLocaleDateString("vi-VN")}
                        <span style={{ marginLeft: 5 }}>×</span>
                      </Button>
                    ))}
                  </Group>
                </div>
              )}
            </div>
            <Text size="xs" c="dimmed" mt="xs">
              Click vào ngày để chọn/bỏ chọn. Bạn có thể chọn nhiều ngày để tạo lịch cùng lúc
            </Text>
          </Fieldset>

          <Fieldset legend="Ca làm việc" mb="md">
            {form.values.shifts.length === 0 ? (
              <Text c="dimmed" size="sm">
                Đang tải ca làm việc...
              </Text>
            ) : (
              form.values.shifts.map((shift, index) => {
                const shiftData = shifts.find(
                  (s) => String(s.id) === shift.shiftId
                );
                if (!shiftData) return null;
                return (
                  <Group key={index} mt="md">
                    <Text fw={500} style={{ minWidth: 100 }}>
                      {shiftData.displayName}
                    </Text>
                    <NumberInput
                      label="Số slot tối đa"
                      min={1}
                      style={{ flex: 1 }}
                      {...form.getInputProps(`shifts.${index}.maxSlots`)}
                    />
                  </Group>
                );
              })
            )}
          </Fieldset>

          <Group justify="flex-end" mt="md">
            <Button variant="subtle" onClick={close}>
              Hủy
            </Button>
            <Button type="submit" loading={loading}>
              Tạo Lịch ({form.values.scheduleDates.length} ngày)
            </Button>
          </Group>
        </form>
      </Modal>

      {/* Modal khóa lịch */}
      <Modal
        opened={lockModalOpened}
        onClose={closeLockModal}
        title="Khóa Lịch Làm Việc"
      >
        <TextInput
          label="Lý do khóa (nghỉ phép, bận việc, ...)"
          placeholder="Nhập lý do"
          {...lockForm.getInputProps("reason")}
          mb="md"
        />
        <Group justify="flex-end">
          <Button variant="subtle" onClick={closeLockModal}>
            Hủy
          </Button>
          <Button onClick={handleConfirmLock} loading={loading} color="red">
            Khóa Lịch
          </Button>
        </Group>
      </Modal>
    </div>
  );
};

export default Schedule;

