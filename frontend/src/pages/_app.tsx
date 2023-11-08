import '@/styles/globals.css'
import '@/styles/ui/form.css'
import '@/styles/ui/cards/cardEvent.css'
import "@/styles/layoutCss/sideBar.css"
import "@/styles/layoutCss/footer.css"
import "@/styles/layoutCss/navbar.css"
import '@/styles/ui/cards/cardUpcoming.css'
import '@/styles/ui/cards/cardShows.css'
import '@/styles/ui/carousel.css'
import '@/styles/ui/searchbar.css'
import BaseLayout from "@/components/layouts/base-layout";


import type { AppProps } from 'next/app'
import { SidebarProvider } from "@/components/context/sidebar-context"
import SessionAuthProvider from '@/components/context/SessionAuthProvider'

export default function App({ Component, pageProps }: AppProps) {
  return (
    <SessionAuthProvider>
      <SidebarProvider>
          <Component {...pageProps} />
      </SidebarProvider>
    </SessionAuthProvider>
  )
}
