@Test
public void testTextVisit() {
  ASTText node = ...;
  assertEquals("Welcome", node.getSTRING());
}

@Test
public void testButtonVisit() {
  ASTButton node = ...;
  assertTrue(node.getSTRING().contains("Click"));
}
