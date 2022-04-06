package zliu.elliot.entity;

public class ServerResponse {

    public static int FAILURE = 1;
    public static int SUCCESS = 0;

    private int status;

    private String message;

    public ServerResponse() {
        this.status = ServerResponse.SUCCESS;
    }

    public ServerResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ServerResponse(int status) {
        this.status = status;
    }

    public boolean ifSuccess(){
        return status == 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFailureMsg(String message) {
        this.status = ServerResponse.FAILURE;
        this.message = message;
    }

    public void setSuccessMsg(String message) {
        this.status = ServerResponse.SUCCESS;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
