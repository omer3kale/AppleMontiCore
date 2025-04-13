import de.monticore.viewdsl._ast.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeneratedVisitorTests {

  @Test
  public void testTextVisit() {
    ASTText node = new ASTText();
    node.setSTRING("Welcome");
    assertEquals("Welcome", node.getSTRING());
  }

  @Test
  public void testButtonVisit() {
    ASTButton node = new ASTButton();
    node.setSTRING("Click here to start");
    assertTrue(node.getSTRING().contains("Click"));
  }

  @Test
  public void testTextFieldVisit() {
    ASTTextField node = new ASTTextField();
    node.setName("state");
    node.setSTRING("Type...");
    assertEquals("state", node.getName());
    assertEquals("Type...", node.getSTRING());
  }

}
