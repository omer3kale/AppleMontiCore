class TranslationViewModel: ObservableObject {
    @Published var translations: [String: String] = [:]

    func load() {
        guard let url = Bundle.main.url(forResource: "ast", withExtension: "json"),
              let data = try? Data(contentsOf: url),
              let decoded = try? JSONDecoder().decode(TranslationData.self, from: data) else {
            return
        }
        translations = decoded.entries
    }
}
