import Foundation

// MARK: - State Enum
enum State: String {
    case kickOff = "KickOff"
    case inPlay = "InPlay"
    case goal = "Goal"
    case gameOver = "GameOver"
}

// MARK: - Transition Struct
struct Transition {
    let from: State
    let event: String
    let to: State
}

// MARK: - Automaton Class
class Automaton {
    let name: String
    private(set) var currentState: State
    private let transitions: [Transition]

    init(name: String, initialState: State, transitions: [Transition]) {
        self.name = name
        self.currentState = initialState
        self.transitions = transitions
    }

    func handleEvent(_ event: String) -> Bool {
        if let transition = transitions.first(where: { $0.from == currentState && $0.event == event }) {
            print("Transitioning from \(currentState.rawValue) to \(transition.to.rawValue) on event '\(event)'")
            currentState = transition.to
            return true
        } else {
            print("No valid transition from \(currentState.rawValue) on event '\(event)'")
            return false
        }
    }
}

// MARK: - Football Automaton Example
let footballTransitions = [
    Transition(from: .kickOff, event: "startGame", to: .inPlay),
    Transition(from: .inPlay, event: "scoreGoal", to: .goal),
    Transition(from: .inPlay, event: "endGame", to: .gameOver),
    Transition(from: .goal, event: "restart", to: .inPlay),
    Transition(from: .goal, event: "endGame", to: .gameOver)
]

let footballAutomaton = Automaton(name: "Football", initialState: .kickOff, transitions: footballTransitions)

// MARK: - Test the Automaton
print("Current State: \(footballAutomaton.currentState.rawValue)")
footballAutomaton.handleEvent("startGame")
footballAutomaton.handleEvent("scoreGoal")
footballAutomaton.handleEvent("restart")
footballAutomaton.handleEvent("endGame")
