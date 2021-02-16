declare global {
  interface PluginRegistry {
    FirebaseUploadFile: FirebaseUploadFilePlugin;
  }
}

export interface FirebaseUploadFilePlugin {
  putStorageFile(options: { filelocalName: string, fileNewStorageName: string, fileNewStorageUrl: string, fileCompress: boolean }): Promise<{ filelocalName: string, fileNewStorageName: string, fileNewStorageUrl: string, fileCompress: boolean }>;
  getStorageDownloadUrl(options: { fileStoragePath: string }): Promise<{ fileStoragePath: string }>;
}
