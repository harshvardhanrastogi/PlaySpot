import SwiftUI
import Firebase
import ComposeApp

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // Handle deeplink by passing to shared Kotlin code
                    DeepLinkHandler.shared.handleDeepLink(uri: url.absoluteString)
                }
        }
    }
}
