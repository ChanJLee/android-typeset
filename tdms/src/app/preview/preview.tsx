"use client";

import { type } from "os";
import "./preview.css";

export class Span {
  text: string | undefined;
  style: {} | undefined;
  region: number[] | undefined
}

export class Line {
  spans: Span[] | undefined;
  space: number | undefined;
}

export class Paragraph {
  padding: number[] | undefined;
  lines: Line[] | undefined;
}

export class TexasDoc {
  textSize: number | undefined;
  width: number | undefined;
  density: number | undefined;
  paragraphs: Paragraph[] | undefined;
}

function renderSpan(box: Span): JSX.Element {
  return (<span style={{display: 'inline'}}>{box.text}</span>);
}

function renderLine(line: Line): JSX.Element {
  if (line.spans == null || line.spans.length == 0) {
    return <></>;
  }

  return (
    <p>
      {line.spans.map((span) => {
        return renderSpan(span);
      })}
    </p>
  );
}

function renderParagraph(paragraph: Paragraph): JSX.Element {
  if (paragraph.lines == null || paragraph.lines.length == 0) {
    return <></>;
  }

  let paragraphStyle = { width: '100%' };
  if (paragraph.padding) {
    paragraphStyle.paddingLeft = `${paragraph.padding[0]}px`
    paragraphStyle.paddingTop = `${paragraph.padding[1]}px`
    paragraphStyle.paddingRight = `${paragraph.padding[2]}px`
    paragraphStyle.paddingBottom = `${paragraph.padding[3]}px`
  }

  return (
    <div style={paragraphStyle}>
      {paragraph.lines.map((line) => {
        return renderLine(line);
      })}
    </div>
  );
}

function render0(doc: TexasDoc): JSX.Element {
  return (
    <div style={{ height: "100%", overflow: 'scroll', resize: 'both' }}>
      {doc.paragraphs?.map((paragraph) => {
        return renderParagraph(paragraph);
      })}
    </div>
  );
}

function render(doc: TexasDoc | undefined): JSX.Element {
  if (
    doc == null ||
    doc == undefined ||
    doc.paragraphs == null ||
    doc.paragraphs.length == 0
  ) {
    return (
      <div
        style={{
          background: "red",
          height: "100%",
          textAlign: "center",
          justifyContent: "center",
          display: "flex",
          alignItems: "center",
          color: "white",
        }}
      >
        <p>render nothing</p>
      </div>
    );
  }

  return render0(doc);
}

export function PreviewView({ doc }: { doc: TexasDoc | undefined }) {
  return (
    <div className="preview_stage">
      <div className="phone_surface">
        <div className="phone_window">
          <div className="decor_window" id="decor">{render(doc)}</div>
        </div>
      </div>
    </div>
  );
}
