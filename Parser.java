/**
 * Tim McGIll
 * 160805190
 * cp471 a3
 */
import java.io. * ;
import java.util. * ;
//symbol table entery <old and not uesd keept in because I was trying new things
class SyntaxTreeNode < ValueType > {
	private String op;
	private SyntaxTreeNode parent = null;
	private ValueType val; // Possible result of computation

	public SyntaxTreeNode(String inputOp) {
		this.op = inputOp;
	}

	public String toString() {
		return this.op;
	}

	public void setValue(ValueType val) {
		this.val = val;
	}

	public ValueType getValue() {
		return this.val;
	}

	// Models operators
	public static class Interior < ValueType > extends SyntaxTreeNode {
		private ArrayList < SyntaxTreeNode > children;

		public Interior(String inputOp) {
			super(inputOp);
			this.children = new ArrayList < SyntaxTreeNode > ();
		}

		public Interior(String inputOp, SyntaxTreeNode...inputChildren) {
			super(inputOp);
			this.children = new ArrayList < SyntaxTreeNode > ();

			for (SyntaxTreeNode currentChild: inputChildren) {
				if (currentChild != null) {
					currentChild.parent = this;
					this.children.add(currentChild);
				}
			}
		}

		public void addChild(SyntaxTreeNode child) {
			this.children.add(child);
		}

		public SyntaxTreeNode getChild(int index) {
			return this.children.get(index);
		}

		public int numChildren() {
			return this.children.size();
		}
	}

	// Models operands
	public static class Leaf < ValueType > extends SyntaxTreeNode {

		public Leaf(String inputOp, ValueType inputVal) {
			super(inputOp);
			this.setValue(inputVal);
		}

	}

}
//Syntax tree
class SyntaxTree {
	private static SyntaxTreeNode root = null;
	private static LinkedList < SyntaxTreeNode > traversalList = null;

	public SyntaxTreeNode.Interior makeInterior(String op) {
		SyntaxTreeNode.Interior node = new SyntaxTreeNode.Interior(op);

		if (root == null) root = node;

		return node;
	}

	public SyntaxTreeNode.Interior makeInterior(String op, SyntaxTreeNode...children) {

		SyntaxTreeNode.Interior node = new SyntaxTreeNode.Interior(op, children);

		if (root == null) root = node;

		//System.out.println(node);
		return node;
	}

	public < ValueType > SyntaxTreeNode.Leaf makeLeaf(String op, ValueType lexValue) {
		SyntaxTreeNode.Leaf node = new SyntaxTreeNode.Leaf(op, lexValue);
		//System.out.println(node);
		return node;
	}

	// Builds the program stack with a post-order traversal of the syntax tree.
	private void buildListPostorder(SyntaxTreeNode startPoint) {
		if (startPoint == null) return;

		if (startPoint instanceof SyntaxTreeNode.Interior) {
			for (int i = 0; i < ((SyntaxTreeNode.Interior) startPoint).numChildren(); i++) {
				buildListPostorder(((SyntaxTreeNode.Interior) startPoint).getChild(i));
			}
		}

		traversalList.push(startPoint);
	}

	// Returns a list containing the intermediate program representation in bottom-up order.
	public LinkedList < SyntaxTreeNode > getTraversalList() {
		if (traversalList == null) {
			traversalList = new LinkedList < SyntaxTreeNode > ();
			buildListPostorder(root);
			return (LinkedList < SyntaxTreeNode > ) traversalList.clone();
		}
		else {
			return (LinkedList < SyntaxTreeNode > ) traversalList.clone();
		}
	}
}
//Token tpyes
class TokenType {
	public String type;
	public TokenType(String t) {
		type = t;
	}
	public String toString() {
		return type;
	}

	public static final TokenType
	RESERVED = new TokenType("reserved"),
	ID = new TokenType("id"),
	INT = new TokenType("integer"),
	DOUBLE = new TokenType("double"),
	COMP = new TokenType("Comps"),
	TERM = new TokenType("terminal"),
	END = new TokenType("end"),
	ERR = new TokenType("error");
}
//SymbolTable Entry
class Entry {
	private String id,
	var, type;
	private Object val;

	public Entry(String i, String v, String t) {
		var = v;
		id = i;
		type = t;
		val = null;
	}
	public String getType() {
		return type;
	}
	public Object getValue() {
		return val;
	}
	public String getName() {
		return var;
	}
	public String getID() {
		return id;
	}

	public boolean equals(Object o) {
		if (o != null && o instanceof Entry) {
			Entry rhs = (Entry) o;
			return ((this.
			var.equals(rhs.
			var)) && (this.type.equals(rhs.type)));
		}

		return false;
	}

	public void setValue(Object val1) {
		val = val1;
	}
}
//class for the symbol table
class SymbolTable {
	private ArrayList < Entry > symbols;
	private String tableName;
	private int count;

	public String getName() {
		return tableName;
	}
	public int getSize() {
		return count;
	}

	public Entry get(String name) {
		for (Entry e: symbols) {
			if (e.getName().equals(name)) {
				return e;
			}
		}

		return null;
	}
	public SymbolTable(String name) {
		tableName = name;
		symbols = new ArrayList < Entry > ();
		count = 16;
	}

	public Entry get(Integer i) {
		return symbols.get(i);
	}

	public ArrayList < Entry > getSymbols() {
		return symbols;
	}

	public void updateVal(Integer index, Object value) {
		symbols.get(index).setValue(value);
	}
	public void updateVal(String varName, Object value) {
		get(varName).setValue(value);
	}

	public boolean contains(Entry entry) {
		for (int i = 0; i < symbols.size(); i++) {
			if (symbols.get(i).equals(entry)) {
				return true;
			}
		}

		return false;
	}

	public void add(Entry entry) {
		if (!symbols.contains(entry)) {
			symbols.add(entry);
			count++;
		}
	}

	public boolean contains(String name) {
		for (int i = 0; i < symbols.size(); i++) {
			if (symbols.get(i).getName().equals(name)) {
				return true;
			}
		}

		return false;
	}
}
class Token {
	public String val;
	public String type;

	public Token(String t, String v) {
		type = t;
		val = v;
	}

	public String getValue() {
		return val;
	}
	public String getType() {
		return type;
	}
	public String getTokenString() {
		if (this.getType().equals("RESERVED")) return "<" + this.getValue() + ">";
		else if (this.getType().equals("END")) return "<END>";
		else if (this.getType().equals("NEWLINE")) return "<NEWLINE>";
		else if (this.getType().equals("SPACE")) return "<SPACE>";
		else if (this.getType().equals("TAB")) return "<TAB>";
		return "<" + this.getType() + ", '" + this.getValue() + "'>";
	}
}
class Node {
	private Node left;
	private Node right;
	private String val = null;
	public SymbolTable symbols;
	private ArrayList < Node > children;

	public Node(String v) {
		val = v;
		left = null;
		right = null;
		children = new ArrayList < Node > ();
		symbols = new SymbolTable(v);
	}

	public Node getLeft() {
		return left;
	}
	public Node getRight() {
		return right;
	}
	public String getValue() {
		return val;
	}
	public ArrayList < Node > getChildren() {
		return children;
	}
	public void setValue(String v) {
		val = v;
	}
	public void setLeft(Node l) {
		left = l;
	}
	public void setRight(Node r) {
		right = r;
	}
	public void setChildren(ArrayList < Node > c) {
		children = c;
	};
	public void addChild(Node n) {
		children.add(n);
	}
}
public class Parser {
	private static Lexer lexer = new Lexer();
	private Token token = null;
	private static Hashtable < String,
	List < String >> FIRST = new Hashtable < String,
	List < String >> ();
	private static Hashtable < String,
	List < String >> FOLLOW = new Hashtable < String,
	List < String >> ();
	private static ArrayList < SymbolTable > funcTable = new ArrayList < SymbolTable > ();
	private static ArrayList < Node > fnodes = new ArrayList < Node > ();
	private Token nextToken = null;

	private String name,
	function;
	private Node currNode;

	private void initializeFIRST() {
		FIRST.put("<program>", Arrays.asList("def", "int", "double", "if", "while", "print", "return", "ID"));
		FIRST.put("<fdecls>", Arrays.asList("def"));
		FIRST.put("<fdecls_r>", Arrays.asList("def"));
		FIRST.put("<fdec>", Arrays.asList("def"));
		FIRST.put("<params>", Arrays.asList("int", "double"));
		FIRST.put("<params_r>", Arrays.asList(","));
		FIRST.put("<fname>", Arrays.asList("ID"));
		FIRST.put("<declarations>", Arrays.asList("int", "double"));
		FIRST.put("<declarations_r>", Arrays.asList("int", "double"));
		FIRST.put("<decl>", Arrays.asList("int", "double"));
		FIRST.put("<type>", Arrays.asList("int", "double"));
		FIRST.put("<var>", Arrays.asList("ID"));
		FIRST.put("<varlist>", Arrays.asList("ID"));
		FIRST.put("<varlist_r>", Arrays.asList(","));
		FIRST.put("<statement_seq>", Arrays.asList("if", "while", "print", "return", "ID"));
		FIRST.put("<statement_seq_r>", Arrays.asList(";"));
		FIRST.put("<statement>", Arrays.asList("if", "while", "print", "return", "ID"));
		FIRST.put("<else>", Arrays.asList("else"));
		FIRST.put("<expr>", Arrays.asList("ID", "NUM", "("));
		FIRST.put("<expr_r>", Arrays.asList("+", "-"));
		FIRST.put("<term>", Arrays.asList("ID", "NUM", "("));
		FIRST.put("<term_r>", Arrays.asList("*", "/", "%"));
		FIRST.put("<factor>", Arrays.asList("ID", "NUM", "("));
		FIRST.put("<factor_r>", Arrays.asList("("));
		FIRST.put("<bexpr>", Arrays.asList("(", "not"));
		FIRST.put("<bexpr_r>", Arrays.asList("or", "(", "not"));
		FIRST.put("<bterm>", Arrays.asList("(", "not"));
		FIRST.put("<bterm_r>", Arrays.asList("and", "(", "not"));
		FIRST.put("<bfactor>", Arrays.asList("(", "not"));
		FIRST.put("<bfactor_r>", Arrays.asList("(", "ID", "NUM"));
		FIRST.put("<comp>", Arrays.asList("<", ">", "==", "<=", ">=", "<>"));
		FIRST.put("<exprseq>", Arrays.asList(",", "ID", "NUM"));
		FIRST.put("<exprseq_r>", Arrays.asList(","));
	}
	private void initializeFOLLOW() {
		FOLLOW.put("<program>", Arrays.asList("$"));
		FOLLOW.put("<fdecls>", Arrays.asList("int", "double", "if", "while", "print", "return", "ID"));
		FOLLOW.put("<fdec>", Arrays.asList(";"));
		FOLLOW.put("<fdec_r>", Arrays.asList(";"));
		FOLLOW.put("<params>", Arrays.asList(")"));
		FOLLOW.put("<params_r>", Arrays.asList(")"));
		FOLLOW.put("<fname>", Arrays.asList("("));
		FOLLOW.put("<declarations>", Arrays.asList("if", "while", "print", "return", "ID"));
		FOLLOW.put("<decl>", Arrays.asList(";"));
		FOLLOW.put("<decl_r>", Arrays.asList(";"));
		FOLLOW.put("<type>", Arrays.asList("ID"));
		FOLLOW.put("<varlist>", Arrays.asList(";", ",", ".", "(", ")", "]", "[", "then", "+", "-", "*", "/", "%", "==", "<>", "<", ">"));
		FOLLOW.put("<varlist_r>", Arrays.asList(";", ",", ".", "(", ")", "]", "[", "then", "+", "-", "*", "/", "%", "==", "<>", "<", ">"));
		FOLLOW.put("<statement_seq>", Arrays.asList(".", "fed", "fi", "od", "else"));
		FOLLOW.put("<statement>", Arrays.asList(".", ";", "fed", "fi", "od", "else"));
		FOLLOW.put("<statement_seq_r>", Arrays.asList(".", ";", "fed", "fi", "od", "else"));
		FOLLOW.put("<opt_else>", Arrays.asList("fi"));
		FOLLOW.put("<expr>", Arrays.asList(".", ";", "fed", "fi", "od", "else", ")", "=", ">", "<", "]"));
		FOLLOW.put("<term>", Arrays.asList(".", ";", "fed", "fi", "od", "else", ")", "=", ">", "<", "]", "+", "-", "*", "/"));
		FOLLOW.put("<term_r>", Arrays.asList(".", ";", "fed", "fi", "od", "else", ")", "=", ">", "<", "]", "+", "-", "*", "/"));
		FOLLOW.put("<var_r>", Arrays.asList(";", ",", ".", "(", ")", "]", "[", "then", "+", "-", "*", "/", "%", "==", "<>", "<", ">"));
		FOLLOW.put("<var>", Arrays.asList(";", ",", ".", "(", ")", "]", "[", "then", "+", "-", "*", "/", "%", "==", "<>", "<", ">"));
		FOLLOW.put("<comp>", Arrays.asList(""));
		FOLLOW.put("<bfactor_r_p>", Arrays.asList("then", "do", ")", "or", "and"));
		FOLLOW.put("<bfactor>", Arrays.asList("then", "do", ")", "or", "and"));
		FOLLOW.put("<bfactor_r>", Arrays.asList("then", "do", ")", "or", "and"));
		FOLLOW.put("<bterm>", Arrays.asList("then", "do", ")", "or", "and"));
		FOLLOW.put("<bterm_r>", Arrays.asList("then", "do", ")", "or", "and"));
		FOLLOW.put("<bexpr>", Arrays.asList("then", "do", ")", "or"));
		FOLLOW.put("<exprseq_r>", Arrays.asList(")"));
		FOLLOW.put("<exprseq>", Arrays.asList(")"));
		FOLLOW.put("<factor>", Arrays.asList(".", ";", "fed", "fi", "od", "else", ")", "=", ">", "<", "]", "+", "-", "*", "/"));
		FOLLOW.put("<factor_r>", Arrays.asList(".", ";", "fed", "fi", "od", "else", ")", "=", ">", "<", "]", "+", "-", "*", "/"));
		FOLLOW.put("<factor_r_p>", Arrays.asList(".", ";", "fed", "fi", "od", "else", ")", "=", ">", "<", "]", "+", "-", "*", "/"));
	}

	public Parser() throws IOException {
		initializeFIRST();
		initializeFOLLOW();
		nextToken();
		nextToken();
	}

	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
		Node root = parser.program();
		new Eval(root);
	}

	public static ArrayList < SymbolTable > getFuncTable() {
		return funcTable;
	}
	public static Node getFuncNode(String name) {
		for (Node n: fnodes) {
			if (n.getValue().equals(name)) {
				return n;
			}
		}
		return null;
	}

	public static SymbolTable getSymbolTable(String funcName) {
		for (SymbolTable s: funcTable) {
			if (s.getName().equals(funcName)) {
				return s;
			}
		}

		return null;
	}

	public static void updateFuncTable(String funcName, String target, Object val) {
		for (SymbolTable s: funcTable) {
			if (s.getName().equals(funcName)) {
				s.updateVal(target, val);
			}
		}
	}

	public static SymbolTable getSymbolT(String name) {
		for (int i = 0; i < funcTable.size(); i++) {
			if (funcTable.get(i).getName().equals(name)) {
				return funcTable.get(i);
			}
		}

		return null;
	}

	public Node program() {
		currNode = new Node("global");
		fdecls();
		declarations();
		statement_seq();
		isMatch('.');
		return currNode;
	}

	public void fdecls() {
		if (checkFIRST("<fdecls>") != null) {
			fdec();
			isMatch(';');
			fdecls_r();
		}
	}

	public void fdec() {
		if (checkFIRST("<fdec>") != null) {
			Node tmp = currNode;
			Node node;
			isMatch("def");
			type();
			node = fname();
			currNode = node;
			isMatch('(');
			params();
			isMatch(')');
			declarations();
			statement_seq();
			fnodes.add(currNode);
			isMatch("fed");
			currNode = tmp;
			function = null;
		}
	}

	public void fdecls_r() {
		if (checkFIRST("<fdecls_r>") != null) {
			fdec();
			isMatch(';');
			fdecls_r();
		}
	}

	public void type() {
		switch (checkFIRST("<type>")) {
		case "int":
			isMatch("int");
			return;

		case "double":
			isMatch("double");
			return;

		default:
			error();
		}
	}

	public Node fname() {
		Node node = null;

		if (checkFIRST("<fname>") != null) {
			name = nextToken.getValue();
			function = name;

			if (getSymbolT("global") == null) {
				funcTable.add(new SymbolTable("global"));
				getSymbolT("global").add(new Entry(Integer.toString(getSymbolT("global").getSize()), name, "FUNC"));

			} else {
				getSymbolT("global").add(new Entry(Integer.toString(getSymbolT("global").getSize()), name, "FUNC"));
			}

			funcTable.add(new SymbolTable(function));
			node = new Node(name);
			isMatch("ID");

		} else {
			error();
		}

		return node;
	}

	public void params() {
		if (checkFIRST("<params>") != null) {
			type();
			var ();
			params_r();
		}
	}

	public void params_r() {
		if (checkFIRST("<params_r>") != null) {
			isMatch(',');
			params();
		}
	}

	public void declarations() {
		if (checkFIRST("<declarations>") != null) {
			decl();
			isMatch(';');
			declarations_r();
		}
	}

	public void declarations_r() {
		if (checkFIRST("<declarations_r>") != null) {
			decl();
			isMatch(';');
			declarations_r();
		}
	}

	public void decl() {
		if (checkFIRST("<decl>") != null) {
			type();
			varlist();
		}
	}

	public void varlist() {
		if (checkFIRST("<varlist>") != null) {
			var ();
			varlist_r();

		} else {
			error();
		}
	}

	public void varlist_r() {
		if (checkFIRST("<varlist_r>") != null) {
			isMatch(',');
			varlist();
		}
	}

	public void statement_seq() {
		if (checkFIRST("<statement_seq>") != null) {
			statement();
			statement_seq_r();
		}
	}

	public void statement_seq_r() {
		if (checkFIRST("<statement_seq_r>") != null) {
			isMatch(';');
			statement_seq();
		}
	}

	public Node statement() {
		Node node = null;
		Node l,
		r,
		tmp;
		Node optElse = new Node("else");

		if (checkFIRST("<statement>") != null) {
			switch (checkFIRST("<statement>")) {
			case "ID":
				l =
				var ();
				node = isMatch('=');
				r = expr();
				node.setLeft(l);
				node.setRight(r);
				currNode.addChild(node);
				return node;

			case "if":
				node = isMatch("if");
				tmp = currNode;
				currNode = node;
				node.setLeft(bexpr());
				isMatch("then");
				statement_seq();
				currNode = optElse;
				optional();
				isMatch("fi");
				node.setRight(optElse);
				currNode = tmp;
				currNode.addChild(node);
				return node;

			case "while":
				node = isMatch("while");
				tmp = currNode;
				currNode = node;
				node.setLeft(bexpr());
				isMatch("do");
				statement_seq();
				isMatch("od");
				currNode = tmp;
				currNode.addChild(node);
				return node;

			case "print":
				node = isMatch("print");
				node.setLeft(expr());
				currNode.addChild(node);
				return node;

			case "return":
				node = isMatch("return");
				node.setLeft(expr());
				currNode.addChild(node);
				return node;

			default:
				return node;
			}
		}

		return node;
	}

	public void optional() {
		if (checkFIRST("<else>") != null) {
			isMatch("else");
			statement_seq();
		}
	}

	public Node
	var () {
		Node node = null;
		if (checkFIRST("<var>") != null) {
			name = nextToken.getValue();

			if (function != null) {
				getSymbolT(function).add(new Entry(Integer.toString(getSymbolT(function).getSize()), name, "VAR"));

			} else {
				if (getSymbolT("global") == null) {
					funcTable.add(new SymbolTable("global"));
					getSymbolT("global").add(new Entry(Integer.toString(getSymbolT("global").getSize()), name, "VAR"));

				} else {
					getSymbolT("global").add(new Entry(Integer.toString(getSymbolT("global").getSize()), name, "VAR"));
				}

			}

			isMatch("ID");
			var_r();
			node = new Node(name);

		} else {
			error();
		}

		return node;
	}

	public void var_r() {
		if (checkFIRST("<var_r>") != null) {
			isMatch('[');
			expr();
			isMatch(']');
		}
	}

	public Node expr() {
		Node node = null;
		if (checkFIRST("<expr>") != null) {
			Node a = term();
			Node b = expr_r();

			if (b == null) {
				node = a;

			} else {
				b.setLeft(a);
				node = b;
			}

		} else {
			error();
		}

		return node;
	}

	public Node expr_r() {
		String grammer = checkFIRST("<expr_r>");
		Node node = null;

		if (grammer != null) {
			if (grammer.equals("+")) {
				node = isMatch('+');
				Node a = term();
				Node b = expr_r();

				if (b == null) {
					node.setRight(a);

				} else {
					b.setLeft(a);
					node.setRight(b);
				}

			} else if (grammer.equals("-")) {
				node = isMatch('-');
				Node a = term();
				Node b = expr_r();

				if (b == null) {
					node.setRight(a);

				} else {
					b.setLeft(a);
					node.setRight(b);
				}

			} else {
				error();
			}
		}

		return node;
	}

	public Node bexpr() {
		Node node = null;
		if (checkFIRST("<bexpr>") != null) {
			Node a = bterm();
			Node b = bexpr_r();

			if (b == null) {
				node = a;

			} else {
				b.setLeft(a);
				node = b;
			}

		} else {
			error();
		}

		return node;
	}

	public Node bexpr_r() {
		Node node = null;

		if (checkFIRST("<bexpr_r>") != null) {
			node = isMatch("or");
			Node a = bterm();
			Node b = bexpr_r();

			if (b == null) {
				node.setRight(a);

			} else {
				b.setLeft(a);
				node.setRight(b);
			}
		}

		return node;
	}

	public Node bterm() {
		Node node = null;
		if (checkFIRST("<bterm>") != null) {
			Node a = bfactor();
			Node b = bterm_r();

			if (b == null) {
				node = a;

			} else {
				b.setLeft(a);
				node = b;
			}

		} else {
			error();
		}

		return node;
	}

	public Node bterm_r() {
		Node node = null;
		if (checkFIRST("<bterm_r>") != null) {
			node = isMatch("and");
			Node a = bfactor();
			Node b = bterm_r();

			if (b == null) {
				node.setRight(a);

			} else {
				b.setLeft(a);
				node.setRight(a);
			}
		}

		return node;
	}

	public Node bfactor() {
		Node node = null;
		switch (checkFIRST("<bfactor>")) {
		case "(":
			isMatch('(');
			node = bfactor_r();
			isMatch(')');
			return node;

		case "not":
			isMatch("not");
			node = bfactor();
			return node;

		default:
			error();
		}

		return node;
	}

	public Node bfactor_r() {
		String grammer = checkFIRST("<bfactor_r>");
		Node node = null;
		if (FIRST.get("<bfactor_r>").contains(grammer) && token.getType() == "COMP") {
			Node l = expr();
			node = comp();
			Node r = expr();
			node.setLeft(l);
			node.setRight(r);

		} else if (FIRST.get("<bfactor_r>").contains(grammer)) {
			bexpr();

		} else {
			error();
		}

		return node;
	}

	public Node comp() {
		Node node = null;
		if (checkFIRST("<comp>") != null) {
			node = isMatch("COMP");

		} else {
			error();
		}

		return node;
	}

	public Node term() {
		Node node = null;
		if (checkFIRST("<term>") != null) {
			Node a = factor();
			Node b = term_r();

			if (b == null) {
				node = a;

			} else {
				b.setLeft(a);
				node = b;
			}

		} else {
			error();
		}

		return node;
	}

	public Node term_r() {
		String grammer = checkFIRST("<term_r>");
		Node node = null;

		if (grammer != null) {
			if (grammer.equals("*")) {
				node = isMatch('*');
				Node a = factor();
				Node b = term_r();

				if (b == null) {
					node.setRight(a);

				} else {
					b.setLeft(a);
					node.setRight(b);
				}

			} else if (grammer.equals("/")) {
				node = isMatch('/');
				Node a = factor();
				Node b = term_r();

				if (b == null) {
					node.setRight(a);

				} else {
					b.setLeft(a);
					node.setRight(b);
				}

			} else if (grammer.equals("%")) {
				node = isMatch('%');
				Node a = factor();
				Node b = term_r();

				if (b == null) {
					node.setRight(a);

				} else {
					b.setLeft(a);
					node.setRight(b);
				}

			} else {
				error();
			}
		}
		return node;
	}
	

	public void exprseq() {
		if (checkFIRST("<exprseq>") != null) {
			currNode.addChild(expr());
			exprseq_r();
		}
	}

	public void exprseq_r() {
		if (checkFIRST("<exprseq_r>") != null) {
			isMatch(',');
			exprseq();
		}
	}

	public void nextToken() {
		nextToken = token;

		try {
			if (token == null || (token != null && token.getType() != "END")) {
				token = lexer.getNextToken();
			}

		} catch(IOException e) {
			e.printStackTrace();
		}
    }
    public Node factor() {
		String grammer = checkFIRST("<factor>");
		Node node = null;
		Node tmp;

		if (grammer != null) {
			if (grammer.equals("ID")) {
				node = isMatch("ID");
				tmp = currNode;
				currNode = node;
				factor_r();
				currNode = tmp;

			} else if (grammer.equals("NUM")) {
				name = nextToken.getValue();
				isMatch("DOUBLE");
				node = new Node(name);

			} else if (grammer.equals("(")) {
				isMatch('(');
				node = expr();
				isMatch(')');

			} else if (grammer.equals("ID")) {
				var ();

			} else {
				error();
			}
		}

		return node;
	}

	public Node factor_r() {
		String grammer = checkFIRST("<factor_r>");
		Node node = null;

		if (grammer != null) {
			if (grammer.equals("(")) {
				isMatch('(');
				exprseq();
				isMatch(')');
			}
		}

		return node;
	}

	public Node isMatch(char c) {
		boolean isMatch = nextToken.getValue().equals(String.valueOf(c));
		Node n = null;

		if (isMatch) {
			n = new Node(String.valueOf(c));
			nextToken();

		} else {
			error();
		}

		return n;
	}

	public Node isMatch(String s) {
		boolean isMatch = false;
		Node n = null;

		if (nextToken.getType() == s) {
			isMatch = true;

		} else if (s == "INT" || s == "DOUBLE") {
			isMatch = true;

		} else {
			isMatch = nextToken.getValue().toLowerCase().equals(s);
		}

		if (isMatch) {
			n = new Node(nextToken.getValue());
			nextToken();

		} else {
			error();
		}

		return n;
	}

	public String checkFIRST(String nonterminal) {
		List < String > grammer = FIRST.get(nonterminal);

		if (grammer != null) {
			if ((nextToken.getType() == "INT" || nextToken.getType() == "DOUBLE") && grammer.contains("NUM")) {
				return "NUM";

			} else if (nextToken.getType() == "ID" && grammer.contains("ID")) {
				return "ID";

			} else if (grammer.contains(nextToken.getValue())) {
				return nextToken.getValue();

			} else {
				return null;
			}

		} else {
			return null;
		}
	}

	public static void error() {
		System.out.println("in Valid Parse");
		System.out.println("Error on Line " + lexer.getLineNum() + " at token ");
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		System.out.println(Arrays.toString(stackTraceElements));
		System.exit(0);
	}
}
class Lexer {
	private static final List < String > RESERVED = Arrays.asList("int", "while", "do", "od", "print", "double", "def", "fed", "return", "if", "fi", "then", "else", "and", "not", "or");
	private static final List < Character > TERMS = Arrays.asList(',', ';', '(', ')', '<', '>', '[', ']', '=', '+', '-', '*', '/', '%', '.');
	private static final List < Character > SPACES = Arrays.asList(' ', '\t', '\r', '\n');
	static final List < String > symbols = new ArrayList < String > ();
	private static char c = ' ';
	int lineNum = 1;

	// Constructor
	public Lexer() {
		for (String str: RESERVED) {
			symbols.add(str);
		}
	}

	public static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z');
	}
	void readchar() throws IOException {
		c = (char) System. in .read();
		System.out.print(c);
	}
	public int getLineNum() {
		return lineNum;
	}

	// Creates and returns the next token from the input code
	public Token getNextToken() throws IOException {
		// Skip whitespace
		while (SPACES.contains(c)) {
			if (c == '\n') {
				lineNum += 1;
			}
			readchar();
		}
		// Check for Comparators
		switch (c) {
		case '=':
			readchar();
			if (c == '=') {
				readchar();
				return new Token("COMP", "==");

			} else {
				return new Token("TERM", "=");
			}

		case '<':

			readchar();
			if (c == '=') {

				readchar();
				return new Token("COMP", "<=");

			} else if (c == '>') {

				readchar();
				return new Token("COMP", "<>");

			} else {
				return new Token("COMP", "<");

			}

		case '>':

			readchar();
			if (c == '=') {
				return new Token("COMP", ">=");

			} else {
				return new Token("COMP", ">");

			}
		}

		// Check if the token is a  string
		if (isLetter(c)) {
			String id = "";
			while (isLetter(c) || isDigit(c)) {
				id += (char) c;
				readchar();

			}

			if (symbols.contains(id)) {
				int index = symbols.indexOf(id) + 1;

				// Check if the token is reserved or not
				if (index < RESERVED.size()) {
					return new Token("RESERVED", id);

				} else {
					return new Token("ID", id);

				}

				// Add token is already known
			} else {
				symbols.add(id);
				return new Token("ID", id);

			}

			// Check if the token is a  number
		} else if (isDigit(c)) {
			String num = "";

			while (isDigit(c)) {
				num += (char) c;
				readchar();

			}

			if (isLetter(c) && c != 'e') {
				while (isLetter(c) || isDigit(c)) {
					num += (char) c;
					readchar();

				}

				return new Token("ERROR", num);

			} else if (c != '.') {
				return new Token("INT", num);

				// Checks if its a double
			} else {
				num += (char) c;
				readchar();

				if (!isDigit(c)) {
					while (isLetter(c) || isDigit(c) || TERMS.contains(c)) {
						num += (char) c;
						readchar();

					}

					return new Token("ERROR", num);
				}

				while (isDigit(c)) {
					num += (char) c;
					readchar();

				}

				// Check scientific notation
				if (c == 'e' || c == 'E') {
					num += (char) c;
					readchar();

					if (c == '-' || c == '+') {
						num += (char) c;
						readchar();

					}

					if (isDigit(c)) {
						while (isDigit(c)) {
							num += (char) c;
							readchar();

						}

						// Is invalid 
					} else {
						return new Token("ERROR", num);
					}

					// Is invalid 
				} else if (isLetter(c)) {
					while (isLetter(c) || isDigit(c) || TERMS.contains(c)) {
						num += (char) c;
						readchar();

					}

					return new Token("ERROR", num);
				}

				// Is valid 
				return new Token("DOUBLE", num);
			}

		} else if (TERMS.contains(c)) {
			if (c == '.') {
				return new Token("END", ".");

			} else {
				Token token = new Token("TERM", String.valueOf(c));

				readchar();
				return token;
			}

		} else {
			Token token = new Token("ERROR", String.valueOf(c));

			readchar();
			return token;
		}
	}
}
class Eval {
	private Node PFN;
	private Node fnode;
	public Node evaluate(Node root) {
		Node node = null;
		for (Node n: root.getChildren()) {
			node = statements(n);
			if (node != null) {
				return node;
			}
		}
		return node;
	}
	public Eval(Node root) {
		if (root != null) {
			fnode = root;
			PFN = null;
			fnode.symbols = Parser.getSymbolTable(root.getValue());
			evaluate(root);
		}
	}

	public Node getStart() {
		return fnode;
	}
	public String getFunName() {
		return fnode.getValue();
	}
	public String getPF() {
		return PFN.getValue();
	}
	public Object expressions(Node n) {
		Object value = null;
		Object intORdub = resolve(n);

		if (n.getValue().equals("+")) {
			Object r = expressions(n.getRight());
			Object l = expressions(n.getLeft());

			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}

			value = numericOperation("+", l, r);

		} else if (n.getValue().equals("-")) {
			Object r = expressions(n.getRight());
			Object l = expressions(n.getLeft());

			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}

			value = numericOperation("-", l, r);

		} else if (n.getValue().equals("/")) {

			Object r = expressions(n.getRight());
			Object l = expressions(n.getLeft());

			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}

			value = numericOperation("/", l, r);

		} else if (n.getValue().equals("*")) {
			Object r = expressions(n.getRight());
			Object l = expressions(n.getLeft());

			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}

			value = numericOperation("*", l, r);

		} else if (n.getValue().equals("%")) {
			Object r = expressions(n.getRight());
			Object l = expressions(n.getLeft());

			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}

			value = numericOperation("%", l, r);

		} else if (Parser.getFuncNode(n.getValue()) != null) {
			SymbolTable table = Parser.getSymbolTable(n.getValue());
			ArrayList < Node > children = n.getChildren();

			for (int i = 0; i < children.size(); i++) {
				table.updateVal(i, expressions(children.get(i)));
			}

			Node node = Parser.getFuncNode(n.getValue());
			node.symbols = table;
			n.symbols = table;
			PFN = fnode;
			fnode = n;

			return expressions(evaluate(node));

		} else if (fnode.symbols.contains(n.getValue())) {
			return fnode.symbols.get(n.getValue()).getValue();

		} else if (intORdub instanceof Integer || intORdub instanceof Double) {
			value = intORdub;

		} else {

			Parser.error();
		}

		return value;
	}
	public Node statements(Node n) {
		Node node = null;
		Object value = null;

		if (n.getValue().equals("=")) {
			value = expressions(n.getRight());
			fnode.symbols.updateVal(n.getLeft().getValue(), value);

		} else if (n.getValue().equals("if")) {
			if (Comp(n.getLeft())) {
				for (int i = 0; i < n.getChildren().size(); i++) {
					node = statements(n.getChildren().get(i));

					if (node != null) {
						return node;
					}
				}

			} else {
				for (int i = 0; i < n.getRight().getChildren().size(); i++) {
					node = statements(n.getRight().getChildren().get(i));

					if (node != null) {
						return node;
					}
				}
			}

		} else if (n.getValue().equals("while")) {
			while (Comp(n.getLeft())) {
				for (int i = 0; i < n.getChildren().size(); i++) {
					node = statements(n.getChildren().get(i));

					if (node != null) {
						return node;
					}
				}
			}

		} else if (n.getValue().equals("print")) {

			value = expressions(n.getLeft());
			System.out.println("");
			System.out.println("-----------------");
			System.out.println("output: " + value);

		} else if (n.getValue().equals("return")) {
			node = new Node(expressions(n.getLeft()).toString());
			fnode.symbols = Parser.getSymbolTable(PFN.getValue());
			fnode = PFN;

		} else if (Parser.getFuncNode(n.getValue()) != null) {
			SymbolTable table = Parser.getSymbolTable(n.getValue());
			ArrayList < Node > children = n.getChildren();

			for (int i = 0; i < children.size(); i++) {
				table.updateVal(i, expressions(children.get(i)));
			}

			node = Parser.getFuncNode(n.getValue());
			node.symbols = table;

			evaluate(node);
		}

		return node;
	}

	public boolean Comp(Node n) {
		boolean condition = false;

		if (n.getValue().equals(">")) {
			Object l = expressions(n.getLeft());
			Object r = expressions(n.getRight());
			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}
			if (Comps(">", l, r)) {
				condition = true;

			} else {
				condition = false;
			}

		} else if (n.getValue().equals("<")) {
			Object l = expressions(n.getLeft());
			Object r = expressions(n.getRight());
			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}
			if (Comps("<", l, r)) {
				condition = true;

			} else {
				condition = false;
			}

		} else if (n.getValue().equals("<=")) {
			Object l = expressions(n.getLeft());
			Object r = expressions(n.getRight());
			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}
			if (Comps("<=", l, r)) {
				condition = true;

			} else {
				condition = false;
			}

		} else if (n.getValue().equals(">=")) {
			Object l = expressions(n.getLeft());
			Object r = expressions(n.getRight());
			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}
			if (Comps(">=", l, r)) {
				condition = true;

			} else {
				condition = false;
			}

		} else if (n.getValue().equals("==")) {
			Object l = expressions(n.getLeft());
			Object r = expressions(n.getRight());

			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}
			if (Comps("==", l, r)) {
				condition = true;

			} else {
				condition = false;
			}

		} else if (n.getValue().equals("<>")) {
			Object l = expressions(n.getLeft());
			Object r = expressions(n.getRight());
			if (l instanceof String) {
				l = (Object) Parser.getSymbolT(getFunName()).get(l.toString()).getValue();
			}
			if (r instanceof String) {
				r = (Object) Parser.getSymbolT(getFunName()).get(r.toString()).getValue();
			}

			if (Comps("<>", l, r)) {
				condition = true;

			} else {
				condition = false;
			}

		}
		return condition;
	}

	public Object resolve(Node n) {
		Object val = null;
		boolean d = false;

		try {
			try {
				val = Integer.parseInt(n.getValue());

			} catch(NumberFormatException e) {
				d = true;
			}

			if (d) {
				val = Double.parseDouble(n.getValue());
			}

		} catch(NumberFormatException e) {
			val = n.getValue();
		}

		return val;
	}

	public Object numericOperation(String op, Object x, Object y) {
		Object result = null;
		if (x instanceof Integer && y instanceof Integer) {
			result = numericOperation(op, (int) x, (int) y);
		}
		else if (x instanceof Double && y instanceof Double) {
			result = numericOperation(op, (double) x, (double) y);
		}
		else {
			Parser.error();
		}

		return result;
	}

	public boolean Comps(String op, Object x, Object y) {
		boolean result = false;
		if (x instanceof Integer && y instanceof Integer) {
			result = Comps(op, (int) x, (int) y);
		}
		else if (x instanceof Double && y instanceof Double) {
			result = Comps(op, (double) x, (double) y);
		}
		else {
			Parser.error();
		}

		return result;
	}

	public boolean Comps(String op, int x, int y) {
		boolean result = false;
		switch (op) {
		case "<":
			result = x < y;
			return result;

		case ">":
			result = x > y;
			return result;

		case "<=":
			result = x <= y;
			return result;

		case ">=":
			result = x >= y;
			return result;

		case "==":
			result = x == y;
			return result;

		case "<>":
			result = x != y;
			return result;
		}

		return result;
	}

	public boolean Comps(String op, double x, double y) {
		boolean result = false;
		switch (op) {
		case "<":
			result = x < y;
			return result;

		case ">":
			result = x > y;
			return result;

		case "<=":
			result = x <= y;
			return result;

		case ">=":
			result = x >= y;
			return result;

		case "==":
			result = x == y;
			return result;

		case "<>":
			result = x != y;
			return result;
		}

		return result;
	}

	public int numericOperation(String op, int x, int y) {
		int result = 0;

		switch (op) {
		case "+":
			result = x + y;
			return result;

		case "-":
			result = x - y;
			return result;

		case "*":
			result = x * y;
			return result;

		case "/":
			result = x / y;
			return result;

		case "%":
			result = x % y;
			return result;
		}

		return result;
	}

	public double numericOperation(String op, double x, double y) {
		double result = 0;

		switch (op) {
		case "+":
			result = x + y;
			return result;

		case "-":
			result = x - y;
			return result;

		case "*":
			result = x * y;
			return result;

		case "/":
			result = x / y;
			return result;

		case "%":
			result = x % y;
			return result;
		}

		return result;
	}
}