﻿using ScreenshotSpace;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ToolsSpace;

namespace ResponseBuilderSpace {

    public class ResponseBuilder {

        //private ScreenshotTaker st = new ScreenshotTaker();

        private string response = "";

        public ResponseBuilder() {
            
        }

        private void add(string t, string s) {
            response += "[" + t + Client.SEPERATOR + s + "],";
        }

        public void id(string id) {
            add("id", id);
        }

        public void sendresponse(bool b) {
            add("response", b.ToString());
        }

        public void msg(string s) {
            add("msg", s + Environment.NewLine);
        }

        public void screenshot() {
            add("screenshot", Tools.EncodeToBase64(Tools.EncodeImageToASCII(Tools.ScaleImage(Tools.TakeScreenshot(), Client.SCREENSHOT_SCALE_FACTOR))));
        }

        public void Clear() {
            response = "";
        }

        public string Build() {
            if(response.Length == 0) {
                return "";
            }
            return response.Substring(0, response.Length - 1);
        }

    }

}
