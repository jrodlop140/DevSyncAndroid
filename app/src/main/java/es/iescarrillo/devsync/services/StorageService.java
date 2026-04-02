package es.iescarrillo.devsync.services;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageService {

    private final StorageReference storageReference;

    public StorageService() {
        storageReference = FirebaseStorage.getInstance().getReference("profile_photos");
    }

    public StorageReference getPhotoReference(String userId) {
        return storageReference.child(userId);
    }

    public void uploadProfilePhoto(String userId, Uri imageUri,
                                   com.google.android.gms.tasks.OnSuccessListener<Uri> successListener,
                                   com.google.android.gms.tasks.OnFailureListener failureListener) {

        StorageReference photoRef = getPhotoReference(userId);

        photoRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}