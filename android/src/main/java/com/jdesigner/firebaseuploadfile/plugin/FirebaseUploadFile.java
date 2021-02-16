package com.jdesigner.firebaseuploadfile.plugin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import android.content.Context;
import java.io.File;
import java.net.URISyntaxException;

@NativePlugin
public class FirebaseUploadFile extends Plugin {
    public interface AsyncResponse {
        void processFinish(Object output);
    }

    @PluginMethod
    public void putStorageFile(final PluginCall call) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final String fileLocalName = call.getString("fileLocalName");
        final String fileNewStorageName = call.getString("fileNewStorageName");
        final String fileNewStorageUrl = call.getString("fileNewStorageUrl");
        final Boolean fileCompress = call.getBoolean("fileCompress");
        final JSObject ret = new JSObject();
        final File dir = this.getContext().getFilesDir();
        final Uri file = Uri.fromFile(new File(dir, fileLocalName));
        String localFilePath = file.getPath();

        try {
            if(fileCompress){
                VideoCompressAsyncTask asyncTask = new VideoCompressAsyncTask(new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        final Uri fileCompressed = Uri.fromFile(new File((String) output));
                        final StorageReference riversRef = storageRef.child(fileNewStorageUrl + fileNewStorageName);
                        final UploadTask uploadTask = riversRef.putFile(fileCompressed);
    
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                String stackTrace = Log.getStackTraceString(exception);
                                                ret.put("status", "error");
                                                ret.put("response", "The file could not be uploaded.");
                                                ret.put("downloadUrl", "");
                                                call.error(stackTrace);
                                                call.reject(stackTrace);
                                            }
                                        }
                                )
                                .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
    
                                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                                StorageReference dateRef = storageRef.child(fileNewStorageUrl + fileNewStorageName);
                                                dateRef
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(
                                                                new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri downloadUrl) {
                                                                        ret.put("status", "success");
                                                                        ret.put("response", "The file was upload and return the public url.");
                                                                        ret.put("downloadUrl", downloadUrl);
                                                                        call.success(ret);
                                                                        call.resolve(ret);
                                                                    }
                                                                }
                                                        ).addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                String stackTrace = Log.getStackTraceString(exception);
                                                                ret.put("status", "success");
                                                                ret.put("response", "the file was upload but not return the public url.");
                                                                ret.put("downloadUrl", "");
                                                                call.success(ret);
                                                                call.resolve(ret);
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                    }
                }, this.getContext());
                asyncTask.execute(localFilePath, Environment.getExternalStorageDirectory().getPath());

        }else{
            final Uri fileNotCompressed = Uri.fromFile(new File(localFilePath));
            final StorageReference riversRef = storageRef.child(fileNewStorageUrl + fileNewStorageName);
            final UploadTask uploadTask = riversRef.putFile(fileNotCompressed);

            uploadTask
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    String stackTrace = Log.getStackTraceString(exception);
                                    ret.put("status", "error");
                                    ret.put("response", "The file could not be uploaded.");
                                    ret.put("downloadUrl", "");
                                    call.error(stackTrace);
                                    call.reject(stackTrace);
                                }
                            }
                    )
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference dateRef = storageRef.child(fileNewStorageUrl + fileNewStorageName);
                                    dateRef
                                            .getDownloadUrl()
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri downloadUrl) {
                                                            ret.put("status", "success");
                                                            ret.put("response", "The file was upload and return the public url.");
                                                            ret.put("downloadUrl", downloadUrl);
                                                            call.success(ret);
                                                            call.resolve(ret);
                                                        }
                                                    }
                                            ).addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    String stackTrace = Log.getStackTraceString(exception);
                                                    ret.put("status", "success");
                                                    ret.put("response", "the file was upload but not return the public url.");
                                                    ret.put("downloadUrl", "");
                                                    call.success(ret);
                                                    call.resolve(ret);
                                                }
                                            }
                                    );
                                }
                            }
                    );
        }
        } catch (Exception e) {
            ret.put("status", "error");
            ret.put("response", e);
            ret.put("downloadUrl", "");
            call.error(e.toString());
            call.reject(e.toString());
        }
    }

    @PluginMethod
    public void getStorageDownloadUrl(final PluginCall call){
        final String value = call.getString("fileStoragePath");
        final JSObject ret = new JSObject();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(value);
        dateRef
            .getDownloadUrl()
            .addOnSuccessListener(
                new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        ret.put("response", downloadUrl);
                        call.resolve(ret);
                    }
                }
            ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        String stackTrace = Log.getStackTraceString(exception);
                        ret.put("response", stackTrace);
                        call.reject(stackTrace);
                    }
                }
            );
    }


    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {
        public AsyncResponse delegate = null; //Call back interface
        Context mContext;

        public VideoCompressAsyncTask(AsyncResponse asyncResponse, Context context ) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return  filePath;
        }

        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            delegate.processFinish(compressedFilePath);
        }
    }

}
