package initiate.helpers;

import com.amazonaws.util.json.Jackson;
import initiate.hyperscience.models.Submission;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class WebClientHelper {

  public static final String EMPTY_JSON_OBJECT = "{}";

  public static <T> Call<ResponseBody> makeCallResponseJson(T response) {
    return new Call<>() {
      @Override
      public Response<ResponseBody> execute() throws IOException {
        return Response.success(
            ResponseBody.create(
                MediaType.parse("application/json"), Jackson.toJsonString(response)));
      }

      @Override
      public void enqueue(Callback<ResponseBody> callback) {}

      @Override
      public boolean isExecuted() {
        return false;
      }

      @Override
      public void cancel() {}

      @Override
      public boolean isCanceled() {
        return false;
      }

      @Override
      public Call<ResponseBody> clone() {
        return null;
      }

      @Override
      public Request request() {
        return new Request.Builder().url("https://www.fakesite.us.gov").build();
      }

      @Override
      public Timeout timeout() {
        return null;
      }
    };
  }

  public static Call<ResponseBody> makeCallResponseByteStream() {
    return new Call<>() {
      @Override
      public Response<ResponseBody> execute() throws IOException {
        return Response.success(ResponseBody.create(null, new byte[0]));
      }

      @Override
      public void enqueue(Callback<ResponseBody> callback) {}

      @Override
      public boolean isExecuted() {
        return false;
      }

      @Override
      public void cancel() {}

      @Override
      public boolean isCanceled() {
        return false;
      }

      @Override
      public Call<ResponseBody> clone() {
        return null;
      }

      @Override
      public Request request() {
        return new Request.Builder().url("https://www.fakesite.us.gov").build();
      }

      @Override
      public Timeout timeout() {
        return null;
      }
    };
  }

  public static Call<Submission> makeCallResponseSubmission(Submission response) {
    return new Call<>() {
      @Override
      public Response<Submission> execute() throws IOException {
        return Response.success(response);
      }

      @Override
      public void enqueue(Callback<Submission> callback) {}

      @Override
      public boolean isExecuted() {
        return false;
      }

      @Override
      public void cancel() {}

      @Override
      public boolean isCanceled() {
        return false;
      }

      @Override
      public Call<Submission> clone() {
        return null;
      }

      @Override
      public Request request() {
        return new Request.Builder().url("https://www.fakesite.us.gov").build();
      }

      @Override
      public Timeout timeout() {
        return null;
      }
    };
  }
}
