"use client";

import "./notifications.css";

export interface Notification {
  element: (key: any) => JSX.Element;
}

export class Message implements Notification {
  text: string;

  constructor(text: string) {
    this.text = `> ${text}`;
  }

  element(key: any): JSX.Element {
    return <p key={key}>{this.text}</p>;
  }
}

export function NotificationView({
  notifications,
}: {
  notifications: Notification[];
}) {
  let list = notifications.map((notification, index) => {
    return notification.element(index);
  });
  return <div className="notification_root">{list}</div>;
}
