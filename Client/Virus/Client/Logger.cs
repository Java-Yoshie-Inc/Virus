using System;

namespace Virus {

    public class Logger {

        public static void Log(string message) {
            DateTime time = DateTime.Now;
            Console.WriteLine("[" + time.ToString() + "] " + message);
        }

        public static void Log(string message, bool nextLine) {
            DateTime time = DateTime.Now;
            Console.Write("[" + time.ToString() + "] " + message);
        }

    }

}
