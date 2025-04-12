views {
  View HomePage {
    VStack {
      Text("State Checker")
      TextField(name: "stateName", placeholder: "Enter state")
      Button("Check") {
        action = "isState(stateName)"
      }
    }
  }

  View ContactPage {
    VStack {
      Text("Contact Us")
      TextField(name: "message", placeholder: "Your message")
      Button("Send") {
        action = "alert('Message sent!')"
      }
    }
  }
}
