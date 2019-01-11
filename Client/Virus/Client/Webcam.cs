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
            if (capture != null) Stop();
            capture = new VideoCapture();
        }

        public void Stop() {
            if (capture == null) return;
            capture.Stop();
            capture.Dispose();
            capture = null;
        }

        public Bitmap GetImage() {
            this.requested = true;
            if (capture == null) Start();
            return capture.QuerySmallFrame().Bitmap;
        }
    }

}
