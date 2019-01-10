using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using ToolsSpace;

namespace ScreenshotSpace {

    public class ScreenshotTaker {

        private List<Screenshot> screenshots = new List<Screenshot>();
        private static readonly int DELAY = 90;

        private long currentIndex;

        public ScreenshotTaker() {
            new Thread(() => Start()).Start();
        }

        private void Start() {
            while(true) {
                new Thread(() => {
                    Screenshot screenshot = new Screenshot(currentIndex);
                    screenshots.Add(screenshot);
                }).Start();
                currentIndex++;
                Thread.Sleep(DELAY);
            }
        }

        public Bitmap GetScreenshot() {
            Screenshot screenshot = null;
            foreach(Screenshot currentScreenshot in screenshots) {
                if(screenshot == null || currentScreenshot.GetID() > screenshot.GetID()) {
                    screenshot = currentScreenshot;
                }
            }
            screenshots.Clear();
            if(screenshot != null) {
                screenshots.Add(screenshot);
                return screenshot.Get();
            }
            return null;
        }

    }

    class Screenshot {

        private readonly long ID;
        private readonly Bitmap screenshot;

        public Screenshot(long id) {
            this.ID = id;
            this.screenshot = Tools.ScaleImage(Tools.TakeScreenshot(), Client.SCREENSHOT_SCALE_FACTOR);
        }

        public long GetID() {
            return ID;
        }

        public Bitmap Get() {
            return screenshot;
        }

    }
    
}
