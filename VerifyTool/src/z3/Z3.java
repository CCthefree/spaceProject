package z3;

import java.util.HashMap;

import programStructure.Constrain;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

/**
 * class of z3 API
 * 
 * @author zengke.cai
 * 
 */
public class Z3 {

	private Solver solver;

	private Context ctx;

	private final int varAmount; // variable amount, equal with the bound 'K'

	private IntExpr[] timeVar; // variables in z3

	private int[] sample; // evaluation of variables, be set by the most
							// recently satisfiable check


	/**
	 * constructor
	 */
	public Z3(int varAmount) {
		this.varAmount = varAmount;
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");

		try {
			this.ctx = new Context(cfg);
			this.solver = this.ctx.MkSolver();
			initialize();

			System.out.println("Z3 initialization success!");
		}
		catch (Z3Exception e) {
			System.out.println("Z3 initialization exception: ");
			e.printStackTrace();
		}
	}


	/**
	 * create z3 integer variable array
	 * 
	 * @throws Z3Exception
	 */
	private void initialize() throws Z3Exception {
		this.timeVar = new IntExpr[this.varAmount];

		for (int i = 0; i < this.varAmount; i++) {
			this.timeVar[i] = this.ctx.MkIntConst("t" + i);
		}

		IntExpr zero = this.ctx.MkInt(0);

		//base constrains
		BoolExpr[] base = new BoolExpr[this.varAmount];
		base[0] = this.ctx.MkEq(this.timeVar[0], zero);
		for(int i = 1; i < this.varAmount; i++){
			base[i] = this.ctx.MkGe(this.timeVar[i], this.timeVar[i-1]);
		}
		this.solver.Assert(base);

		this.sample = new int[this.varAmount];
	}


	/**
	 * add new model constrain to the solver
	 * 
	 * @param cons
	 *            : constrain in model constrain form, defined in
	 *            'Constrain.java'
	 * @throws Z3Exception
	 */
	public void addCons(Constrain cons) throws Z3Exception {
		BoolExpr z3Cons = transCons(cons);
		this.solver.Assert(z3Cons);
	}


	/**
	 * add new model constrain to the solver
	 * 
	 * @param cons
	 *            : model constrain in negative form
	 * @throws Z3Exception
	 */
	public void addNegCons(Constrain cons) throws Z3Exception {
		BoolExpr z3Cons = transCons(cons);
		BoolExpr negCons = this.ctx.MkNot(z3Cons);
		this.solver.Assert(negCons);
	}


	/**
	 * transform model constrain to constrains in z3 form
	 * 
	 * @param cons
	 *            : constrain in model constrain form, defined in
	 *            'Constrain.java'
	 * @throws Z3Exception
	 */
	private BoolExpr transCons(Constrain cons) throws Z3Exception {
		String op = cons.getOp();
		int value = cons.getValue();
		ArithExpr wholeExpr;

		if (cons.getFirstArray() == null) { // addPart and subPart is integer
			int i = cons.getFirstVar();
			int j = cons.getSecondVar();
			
			//XXX -1 stand for time point zero
			if (j == -1)
				wholeExpr = this.ctx.MkSub(new ArithExpr[] { this.timeVar[i], this.ctx.MkInt(0) });
			else
				wholeExpr = this.ctx.MkSub(new ArithExpr[] { this.timeVar[i], this.timeVar[j] });
		}
		else { // addPart and subPart is integer array
			int[] addPart = cons.getFirstArray();
			int[] subPart = cons.getSecondArray();
			ArithExpr[] add = new ArithExpr[addPart.length];
			ArithExpr[] sub = new ArithExpr[subPart.length];
			for (int i = 0; i < addPart.length; i++) {
				add[i] = this.timeVar[addPart[i]];
			}
			for (int i = 0; i < subPart.length; i++) {
				sub[i] = this.timeVar[subPart[i]];
			}

			wholeExpr = this.ctx
					.MkSub(new ArithExpr[] { this.ctx.MkAdd(add), this.ctx.MkAdd(sub) });
		}
		IntExpr val = this.ctx.MkInt(value);

		// create constrain according to 'op'
		BoolExpr z3cons;
		if (op.equals(">"))
			z3cons = this.ctx.MkGt(wholeExpr, val);
		else if (op.equals(">="))
			z3cons = this.ctx.MkGe(wholeExpr, val);
		else if (op.equals("<"))
			z3cons = this.ctx.MkLt(wholeExpr, val);
		else if (op.equals("<="))
			z3cons = this.ctx.MkLe(wholeExpr, val);
		else
			z3cons = this.ctx.MkEq(wholeExpr, val);

		return z3cons;
	}


	/**
	 * check constrains in solver
	 * 
	 * @return true if it is satisfiable, otherwise false
	 * @throws Z3Exception
	 */
	public boolean check() throws Z3Exception {
		if (this.solver.Check() == Status.SATISFIABLE) {

			Model model = this.solver.Model();
			for (int i = 0; i < this.varAmount; i++) {
				// set 'sample'
				if (model.Evaluate(this.timeVar[i], false).IsInt())
					this.sample[i] = Integer.parseInt(model.Evaluate(this.timeVar[i], false)
							.toString());
			}
			return true;

		}
		else {
			return false;
		}
	}


	/**
	 * get an evaluation of a satisfiable constrains
	 * 
	 * @return
	 */
	public int[] getSample() {
		return this.sample;
	}


	/**
	 * create a backtracking point
	 */
	public void push() {
		try {
			this.solver.Push();
		}
		catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * backtrack
	 */
	public void pop() {
		try {
			this.solver.Pop();
		}
		catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Z3Exception {
		Z3 z = new Z3(20);
		z.push();
		z.addCons(new Constrain(1, 0, ">=", 3));
		z.addCons(new Constrain(2, 1, ">", 5));
		z.addCons(new Constrain(3, 2, ">", 1));
		if (z.check()) {
			System.out.println("satisfiable");
			int[] sample = z.getSample();
			for (int i = 0; i < sample.length; i++)
				System.out.print(sample[i] + "  ");
			System.out.println();
		}
		else
			System.out.println("unsatisfiable");

		z.push();
		z.addCons(new Constrain(new int[] { 0, 1 }, new int[] { 3, 2 }, ">=", 0));
		if (z.check()) {
			int[] sample = z.getSample();
			for (int i = 0; i < sample.length; i++)
				System.out.print(sample[i] + "  ");
			System.out.println();
		}
		else
			System.out.println("unsatisfiable");

		z.pop();

		z.addCons(new Constrain(new int[] { 0 }, new int[] { 1, 2 }, "==", 100));
		if (z.check()) {
			System.out.println("satisfiable");
			int[] sample = z.getSample();
			for (int i = 0; i < sample.length; i++)
				System.out.print(sample[i] + "  ");
			System.out.println();
		}
		else
			System.out.println("unsatisfiable");

		z.pop();
		if (z.check()) {
			System.out.println("satisfiable");
			int[] sample = z.getSample();
			for (int i = 0; i < sample.length; i++)
				System.out.print(sample[i] + "  ");
			System.out.println();
		}
		else
			System.out.println("unsatisfiable");
	}
}
