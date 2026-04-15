import java.io.IOException;
import java.util.*;

/**
 * This SyntaxAnalyser method extends the AbstractSyntaxAnalyser class.
 * I am implementing a Syntax Analyser for the java programming language using
 * recursive descent
 * recogniser
 *
 * @Author: Oghenetega Maseli
 *
 **/

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    private String file;
    private ArrayList<String> list = new ArrayList<String>();

    public SyntaxAnalyser(String file) {
        this.file = file;
        try {
            // Here i initalised the lexical analyser
            lex = new LexicalAnalyser(file);

        } catch (Exception e) {
            // catch clause in case of errors with the LexicalAnalyser
            System.err.println("Failed to load lexical analyser.");
        }
    }

    /**
     * I made this method to generate the error as a String which is then
     * passed as a parameter inside CompilationException error catch block
     * 
     * @param String
     * @param Token
     *
     */
    private String generateErrorString(String expected, Token next) {
        return "line " + next.lineNumber + " in " + this.file + ":\n\t\t\t- Expected token(s) " + expected
                + " but found (" + Token.getName(next.symbol) + ").\n";
    }

    /**
     * Beginning the processing of the top level (first) token.
     * 
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        // Parsing starts here
        myGenerate.commenceNonterminal("StatementPart");
        try {
            // looking for the 'begin' command
            acceptTerminal(Token.beginSymbol);
            // Code enters StatementList
            StatementList();
        } catch (CompilationException e) {
            // generated error
            throw new CompilationException(generateErrorString(" StatementList ", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.endSymbol); // Parsing finished at this point
        myGenerate.finishNonterminal("StatementPart");
    }

    /**
     * This method is made to process the Statement list
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void StatementList() throws IOException, CompilationException {
        // Code begins to read StatementList
        myGenerate.commenceNonterminal("StatementList");
        try {
            // Opens and enters Statement
            Statement();
        } catch (CompilationException e) {
            // error generated
            throw new CompilationException(generateErrorString("StatementList", nextToken), nextToken.lineNumber, e);
        }

        // If the nextToken symbol is a semicolon, then accept the semicolon and then
        // enter the StatementList using recursion
        while (nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);
            try {
                // Enters the StatementList
                StatementList();
            } catch (CompilationException e) {
                // error generated
                throw new CompilationException(generateErrorString("StatementList", nextToken), nextToken.lineNumber,
                        e);
            }
        }
        // end of the code reading the StatementList
        myGenerate.finishNonterminal("StatementList");
    }

    /**
     * Code begins processing Statement
     * selects non-terminals
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void Statement() throws IOException, CompilationException {
        // Code starts reading Statement here
        myGenerate.commenceNonterminal("Statement");
        try {
            // checking our token symbol
            switch (nextToken.symbol) {
                case Token.callSymbol: // Its a Procedure Statement
                    procedure();
                    break;
                case Token.identifier: // Its an Assignment Statement
                    assignment();
                    break;
                case Token.whileSymbol: // Its a While Statement
                    while_st();
                    break;
                case Token.ifSymbol: // Its an If Statement
                    if_st();
                    break;
                case Token.untilSymbol: // Its an Until Statement
                    until_st();
                    break;
                case Token.forSymbol: // Its a For Statement
                    for_st();
                    break;
                default: // None of the specified cases were true, handle the error
                    myGenerate.reportError(nextToken,
                            "Expected <if Statement>, <assignment Statement>, <until Statement>, <while Statement> or <procedure Statement>");
                    break;
            }
        } catch (CompilationException e) {
            // error on token symbol
            throw new CompilationException(
                    generateErrorString("<if>, <assignment>, <until>, <while> or <procedure>", nextToken),
                    nextToken.lineNumber, e);
        }
        // Code is done reading Statement
        myGenerate.finishNonterminal("Statement");
    }

    /**
     * Code begins processing assignment
     * 
     * @throws IOException
     * @throws CompilationException
     */

    /**
     * Code begins processing IF Statement
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void if_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("if Statement");

        acceptTerminal(Token.ifSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<condition>", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.thenSymbol);

        try {
            StatementList();

            if (nextToken.symbol == Token.elseSymbol) {

                acceptTerminal(Token.elseSymbol);
                StatementList();
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("Statement list", nextToken), nextToken.lineNumber, e);
        }

        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        myGenerate.finishNonterminal("if Statement");
    }

    /**
     * Code begins processing While Statement
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void while_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("WhileStatement");

        acceptTerminal(Token.whileSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("condition", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.loopSymbol);

        try {
            StatementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("StatementList", nextToken), nextToken.lineNumber, e);
        }

        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("WhileStatement");
    }

    /**
     * Code begins processing procedure
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void procedure() throws IOException, CompilationException {
        // Code starts reading procedure
        myGenerate.commenceNonterminal("ProcedureStatement");
        // callSymbol and identifier
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);

        // ( argumentList )
        acceptTerminal(Token.leftParenthesis);
        try {
            argumentList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("argument list", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.rightParenthesis);
        // Finishes reading procedure
        myGenerate.finishNonterminal("ProcedureStatement");
    }

    /**
     * Code begins processing Until Statement
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void until_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("until Statement");

        acceptTerminal(Token.doSymbol);

        try {
            StatementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("Statement list", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.untilSymbol);

        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("condition", nextToken), nextToken.lineNumber, e);
        }
        myGenerate.finishNonterminal("until Statement");
    }

    /**
     * Code begins processing for Statement
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void for_st() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("for Statement");

        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        try {
            assignment();
            acceptTerminal(Token.semicolonSymbol);
            condition();
            acceptTerminal(Token.semicolonSymbol);
            assignment();

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("Statement list", nextToken), nextToken.lineNumber, e);
        }
        acceptTerminal(Token.rightParenthesis);

        acceptTerminal(Token.doSymbol);
        try {
            StatementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("Statement list", nextToken), nextToken.lineNumber, e);
        }

        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("for Statement");
    }

    /**
     * Code begins processing argumentList
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void argumentList() throws IOException, CompilationException {
        // Code starts reading factor
        myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        try {
            // checks comma, and the enters argumentList (recursive)
            if (nextToken.symbol == Token.commaSymbol) {
                acceptTerminal(Token.commaSymbol);
                argumentList();
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("ArgumentList", nextToken), nextToken.lineNumber, e);
        }
        // Finishes reading factor
        myGenerate.finishNonterminal("ArgumentList");
    }

    /**
     * Code begins processing condition
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void condition() throws IOException, CompilationException {
        // Code starts reading condition
        myGenerate.commenceNonterminal("condition");
        acceptTerminal(Token.identifier);
        try {
            // Enters conditionalOperator
            conditionalOperator();

            switch (nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.stringConstant:
                    acceptTerminal(Token.stringConstant);
                    break;
                default:
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("condition operator", nextToken), nextToken.lineNumber,
                    e);
        }
        // Finishes reading condition
        myGenerate.finishNonterminal("condition");
    }

    /**
     * Code begins processing conditionalOperator
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void conditionalOperator() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("ConditionalOperator");
        switch (nextToken.symbol) {
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            default:
        }
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    /**
     * Return boolean based on whether the next token is part of an expression
     *
     * @return boolean
     */
    private boolean plusOrMinus(Token token) {
        return token.symbol == Token.plusSymbol
                || token.symbol == Token.minusSymbol;
    }

    /**
     * Return boolean based on whether the next token is part of a factor
     *
     * @return boolean
     */
    private boolean mulOrDiv(Token token) {
        return token.symbol == Token.divideSymbol
                || token.symbol == Token.timesSymbol;
    }

    /**
     * Code begins processing expression
     * 
     * @throws IOException
     * @throws CompilationException
     */

    private void expression() throws IOException, CompilationException {
        // Code starts reading expression
        myGenerate.commenceNonterminal("expression");

        try {
            // Enters term
            term();

            // checks if next token is + or -
            if (plusOrMinus(nextToken)) {
                // accepts the symbol(+ or -)
                acceptTerminal(nextToken.symbol);
                expression();
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("term", nextToken), nextToken.lineNumber, e);
        }
        // Finishes reading expression
        myGenerate.finishNonterminal("expression");
    }

    private void assignment() throws IOException, CompilationException {
        // Code starts processing assignment here
        myGenerate.commenceNonterminal("AssignmentStatement");

        String str = nextToken.text;
        // accepts identifier and then :=
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);

        // Code checks if the next symbol is StringConstant and then adds
        // variable(declare), given its not been declared before
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
            if (list.contains(str) == false) {
                // if variable not declared, then declare variable here
                Variable v = new Variable(str, Variable.Type.STRING); // String Variable
                myGenerate.addVariable(v);
                list.add(str); // adds the declared variables to ArrayList
            }
            myGenerate.finishNonterminal("AssignmentStatement");
            return;
        } else {

            try {
                expression();
                if (list.contains(str) == false) {
                    Variable v = new Variable(str, Variable.Type.NUMBER); // Number Variable
                    myGenerate.addVariable(v);
                    list.add(str);
                }
            } catch (CompilationException e) {
                throw new CompilationException(generateErrorString("expression", nextToken), nextToken.lineNumber, e);
            }
        }
        // Finishes reading assignment
        myGenerate.finishNonterminal("AssignmentStatement");
    }

    /**
     * Code begins processing term
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void term() throws IOException, CompilationException {
        // Code starts reading term
        myGenerate.commenceNonterminal("Term");

        try {
            // Enters factor
            factor();

            // checks if next token is * or /
            while (mulOrDiv(nextToken)) {
                // accepts the symbol(* or /)
                acceptTerminal(nextToken.symbol);
                // Enters term, recursive
                term();
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("factor", nextToken), nextToken.lineNumber, e);
        }
        // Finishes reading term
        myGenerate.finishNonterminal("Term");
    }

    /**
     * Code begins processing factor
     * 
     * @throws IOException
     * @throws CompilationException
     */
    private void factor() throws IOException, CompilationException {
        // Code starts reading factor
        myGenerate.commenceNonterminal("Factor");
        try {
            // Can switch to identifier or numberConstant or (expression)
            switch (nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.leftParenthesis:
                    acceptTerminal(Token.leftParenthesis);
                    expression();
                    acceptTerminal(Token.rightParenthesis);
                default:
                    myGenerate.reportError(nextToken,
                            "Error on factor, expected IDENTIFIER, NUMBER or (expression), but found "
                                    + Token.getName(nextToken.symbol));
                    break;
            }

        } catch (CompilationException e) {
            throw new CompilationException(
                    generateErrorString("identifier, number constant or ( <expression> )", nextToken),
                    nextToken.lineNumber, e);
        }
        // Finishes reading factor
        myGenerate.finishNonterminal("Factor");
    }

    /**
     * @param symbol
     * @throws IOException
     * @throws CompilationException
     */
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {

        Token actual = nextToken;
        if (symbol == actual.symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            return;
        }
        myGenerate.reportError(nextToken, generateErrorString("<" + Token.getName(symbol) + ">", nextToken));
    }

}
