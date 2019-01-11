using System.Text;
using System.Drawing;
using System.Windows.Forms;
using System.IO;

namespace Virus {

    public class Tools {

        public static Bitmap ScaleImage(Bitmap image, float scaleFactor) {
            return new Bitmap(image, new Size((int)(image.Width * scaleFactor), (int)(image.Height * scaleFactor)));
        }

        public static Bitmap TakeScreenshot() {
            Bitmap image = new Bitmap(Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height);
            Graphics screenshotGraphics = Graphics.FromImage(image);
            screenshotGraphics.CopyFromScreen(0, 0, 0, 0, new Size(image.Width, image.Height));
            screenshotGraphics.Flush();
            return image;
        }

        public static byte[] EncodeImageToASCII(Image img) {
            ImageConverter converter = new ImageConverter();
            return (byte[])converter.ConvertTo(img, typeof(byte[]));
        }

        public static string EncodeToBase64(byte[] input) {
            return System.Convert.ToBase64String(input);
        }

        public static byte[] DecodeFromBase64(string input) {
            return System.Convert.FromBase64String(input);
        }

        public static byte[] EncodeToASCII(string input) {
            return Encoding.ASCII.GetBytes(input);
        }

        public static string DecodeFromASCII(byte[] input) {
            return Encoding.ASCII.GetString(input);
        }

        public static void CopyDirectory(string sourceDirName, string destDirName, bool copySubDirs) {
            // Get the subdirectories for the specified directory.
            DirectoryInfo dir = new DirectoryInfo(sourceDirName);

            if (!dir.Exists) {
                throw new DirectoryNotFoundException(
                    "Source directory does not exist or could not be found: "
                    + sourceDirName);
            }

            DirectoryInfo[] dirs = dir.GetDirectories();
            // If the destination directory doesn't exist, create it.
            if (!Directory.Exists(destDirName)) {
                Directory.CreateDirectory(destDirName);
            }

            // Get the files in the directory and copy them to the new location.
            FileInfo[] files = dir.GetFiles();
            foreach (FileInfo file in files) {
                string temppath = Path.Combine(destDirName, file.Name);
                file.CopyTo(temppath, true);
            }

            // If copying subdirectories, copy them and their contents to new location.
            if (copySubDirs) {
                foreach (DirectoryInfo subdir in dirs) {
                    string temppath = Path.Combine(destDirName, subdir.Name);
                    CopyDirectory(subdir.FullName, temppath, copySubDirs);
                }
            }
        }


    }

}
