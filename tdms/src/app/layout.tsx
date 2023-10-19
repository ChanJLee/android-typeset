import './globals.css'
import { Inter } from 'next/font/google'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'Texas Debug Monitor Server',
  description: 'debug tools',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="cn">
      <body className={inter.className}>{children}</body>
    </html>
  )
}
