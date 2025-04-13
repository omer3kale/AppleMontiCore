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

  @Test
  public void testNavigationLinkVisit() {
    ASTNavigationLink node = new ASTNavigationLink();
    node.setLabel("Go Home");
    node.setTarget("HomePage");
    assertEquals("Go Home", node.getLabel());
    assertEquals("HomePage", node.getTarget());
  }

  @Test
  public void testVStackVisit() {
    ASTVStack node = new ASTVStack();
    assertNotNull(node.getComponentList());
    assertTrue(node.getComponentList().isEmpty());
  }

  @Test
  public void testHtmlElementVisit() {
    ASTHtmlElement node = new ASTHtmlElement();
    node.setTagName("p");
    ASTHtmlContent content = new ASTHtmlContent();
    content.setSTRING("Hello");
    node.setHtmlContent(content);
    assertEquals("p", node.getTagName());
    assertTrue(node.isPresentHtmlContent());
    assertEquals("Hello", node.getHtmlContent().getSTRING());
  }

}
