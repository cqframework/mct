import { WarningOutlined, ExclamationCircleOutlined, InfoCircleOutlined } from '@ant-design/icons';

const SeverityIcon = ({ severity }) => {
  switch (severity) {
    case 'warning':
      return <WarningOutlined title="Warning" style={{ color: 'orange' }} />;
    case 'error':
      return <ExclamationCircleOutlined title="Error" style={{ color: 'red' }} />;
    case 'information':
      return <InfoCircleOutlined title="Info" style={{ color: '#1890ff' }} />;
  }
};
export default SeverityIcon;
