﻿using OpenHardwareMonitor.Hardware;
using System;
using System.Diagnostics;

namespace Virus {

    public class Hardware
    {

        public static string Monitor()
        {
            string result = "";

            Computer computer = new Computer();
            computer.CPUEnabled = true;
            computer.GPUEnabled = true;
            computer.Open();

            for (int i = 0; i < computer.Hardware.Length; i++)
            {
                for (int j = 0; j < computer.Hardware[i].Sensors.Length; j++)
                {
                    string data = computer.Hardware[i].Sensors[j].SensorType + " " + computer.Hardware[i].Sensors[j].Name;
                    data += ":" + computer.Hardware[i].Sensors[j].Value.ToString();
                    result += data + Environment.NewLine;
                }
            }
            computer.Close();

            result += "Total RAM:" + (GC.GetTotalMemory(false) / 100);

            float memory = new PerformanceCounter("Memory", "Available Bytes", null).RawValue / 1000000;
            result += Environment.NewLine + "Load RAM:" + (int) (memory);

            return result;
        }

    }

}