"use client";

import {
  Message,
  NotificationView,
  Notification,
} from "./notifications/notifications";
import { Line, Paragraph, PreviewView, TexasDoc, Span } from "./preview/preview";
import { ChartView } from "./chart/chart";
import { useState } from "react";
import { mockV1 } from "./mock";

export default function Home() {
  const [notifications, setNotifications] = useState<Notification[]>([new Message("Hello, welcome to TDMS.")]);
  const [doc, setDoc] = useState<TexasDoc>();

  function sendNotificatio0(notification: Notification) {
    let [...copy] = notifications;
    copy.push(notification);
    setNotifications(copy);
  }

  function sendMessage(text: string) {
    sendNotificatio0(new Message(text));
  }

  function onClicked() {
    let decor = document.getElementById("decor")
    let decorWidth = decor?.offsetWidth

    let json = mockV1();
    let doc = JSON.parse(json)
  
    setDoc(doc)
    sendMessage("开始渲染")
  }

  return (
    <main className="h-[100vh] flex">
      <div className="w-[35%] region">
        <PreviewView doc={doc} />
      </div>

      <div className="flex flex-col w-[65%]">
        <div className="h-[70%] region">
          <ChartView />
        </div>

        <div className="h-[30%] region" onClick={onClicked}>
          <NotificationView notifications={notifications} />
        </div>
      </div>
    </main>
  );
}
