import { useRef, useState } from "react";
import { IconCloudUpload, IconDownload, IconX } from "@tabler/icons-react";
import { Button, Group, Text, useMantineTheme } from "@mantine/core";
import { Dropzone, FileWithPath, MIME_TYPES } from "@mantine/dropzone";
import classes from "./DropzoneButton.module.css";
import { uploadMedia } from "../../../Service/MediaService";

export function DropzoneButton({ close, form, profilePictureId }: any) {
  const theme = useMantineTheme();
  const openRef = useRef<() => void>(null);
  const [file, setFile] = useState<FileWithPath | null>(null);
  const [fileId, setFileId] = useState<string | null>(null);

  const handleDrop = async (files: FileWithPath[]) => {
    setFile(files[0]);
    uploadMedia(files[0])
      .then((data) => {
        console.log("Tải ảnh thành công");
        setFileId(data.id);
      })
      .catch((error) => {
        console.error("Lỗi tải ảnh: ", error);
      });
  };

  const handleSave = () => {
    form.setFieldValue("profilePictureId", fileId);
    close();
  };

  return (
    <div className={classes.wrapper}>
      {!file ? (
        <Dropzone
          openRef={openRef}
          onDrop={handleDrop}
          multiple={false}
          className={classes.dropzone}
          radius="md"
          accept={[MIME_TYPES.png, MIME_TYPES.jpeg]} // chỉ chấp nhận file PDF
          maxSize={5 * 1024 ** 2} // tối đa 30MB
        >
          <div style={{ pointerEvents: "none" }}>
            <Group justify="center">
              {/* Icon khi file hợp lệ */}
              <Dropzone.Accept>
                <IconDownload
                  size={50}
                  color={theme.colors.blue[6]}
                  stroke={1.5}
                />
              </Dropzone.Accept>

              {/* Icon khi file bị từ chối */}
              <Dropzone.Reject>
                <IconX size={50} color={theme.colors.red[6]} stroke={1.5} />
              </Dropzone.Reject>

              {/* Icon khi chưa có hành động */}
              <Dropzone.Idle>
                <IconCloudUpload
                  size={50}
                  stroke={1.5}
                  className={classes.icon}
                />
              </Dropzone.Idle>
            </Group>

            {/* Dòng chữ hướng dẫn */}
            <Text ta="center" fw={700} fz="lg" mt="xl">
              <Dropzone.Accept>Thả ảnh vào đây để tải lên</Dropzone.Accept>
              <Dropzone.Reject>Chỉ chấp nhận ảnh nhỏ hơn 5MB</Dropzone.Reject>
              <Dropzone.Idle>Tải lên ảnh hồ sơ</Dropzone.Idle>
            </Text>

            <Text className={classes.description}>
              Kéo và thả ảnh vào đây để tải lên. Hệ thống chỉ chấp nhận tệp{" "}
              <i>.png & .jpg</i> có dung lượng nhỏ hơn <b>5MB</b>.
            </Text>
          </div>
        </Dropzone>
      ) : (
        <img
          src={URL.createObjectURL(file)}
          alt="Preview"
          className="classes.imagePreview"
        />
      )}

      {!file ? (
        <Button
          className={classes.control}
          size="md"
          radius="xl"
          onClick={() => openRef.current?.()}
        >
          Chọn Ảnh
        </Button>
      ) : (
        <div className="flex gap-3 mt-3 justify-center">
          <Button
            size="md"
            radius="xl"
            color="red"
            onClick={() => openRef.current?.()}
          >
            Thay đổi Ảnh
          </Button>
          <Button size="md" radius="xl" onClick={handleSave} color="green">
            Lưu
          </Button>
        </div>
      )}
    </div>
  );
}
