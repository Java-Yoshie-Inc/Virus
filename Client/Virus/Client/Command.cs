using System;
using System.Diagnostics;

namespace CommandSpace
{
    public class Command
    {

        private string commands;

        public Command(string commands)
        {
            this.commands = commands;
        }

        public string Run()
        {
            Process process = new Process();

            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.WindowStyle = ProcessWindowStyle.Hidden;
            startInfo.FileName = "cmd.exe";

            startInfo.Arguments = "/C " + commands;

            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardInput = true;

            process.StartInfo = startInfo;
            process.Start();

            string output = process.StandardOutput.ReadToEnd();
            process.WaitForExit();

            if(output.Length == 0)
            {
                output = " ";
            }
            return output;
        }
    }

    public class Logger
    {

        public static void Log(string message)
        {
            DateTime time = DateTime.Now;
            Console.WriteLine("[" + time.ToString() + "] " + message);
        }

        public static void Log(string message, bool nextLine)
        {
            DateTime time = DateTime.Now;
            Console.Write("[" + time.ToString() + "] " + message);
        }
    }

}
