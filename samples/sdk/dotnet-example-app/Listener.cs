using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace VeritySDK.Sample
{
    public class Listener
    {
        public delegate void OnListen(string message);

        int port;
        OnListen handler;
        private HttpListener listener = null;

        public Listener(int port, OnListen handler)
        {
            this.port = port;
            this.handler = handler;
        }

        public void listen()
        {
            var prefix = $"http://*:{port}/";
            listener = new HttpListener();
            listener.Prefixes.Add(prefix);
            try
            {
                listener.Start();
                App.consoleOutput($"Started listening...");
            }
            catch (HttpListenerException ex)
            {
                App.consoleOutput(ex.Message);
                return;
            }

            Task.Run(() => { ProcessRequest(); });
            
        }

        public void stop()
        {
            listener.Stop();
            listener.Close();
        }

        private void ProcessRequest()
        {
            while (listener.IsListening)
            {
                var context = listener.GetContext();

                // Get the data from the HTTP stream
                var data = new StreamReader(context.Request.InputStream).ReadToEnd();

                // Process 
                handler(data);

                //Answer
                byte[] b = Encoding.UTF8.GetBytes("Success");
                context.Response.StatusCode = 200;
                context.Response.KeepAlive = false;
                context.Response.ContentLength64 = b.Length;

                var output = context.Response.OutputStream;
                output.Write(b, 0, b.Length);
                context.Response.Close();
            }
        }
    }

}
