# Squirrel Cage Mapper

## What is it?

This is a proof-of-concept.  It probably doesn't solve a problem that you have.  Unless...

- You have one codebase for the whole planet
- You have a different database instance for each region
- You have the same database schema for each region
- Inbound requirements are too arbitrary and region-specific to warrant altering your global schema for each one
- You're OK with configuring these things via database updates (and not a UI)
- Your IDE-to-production pipeline is slower than the rate at which your requirements change
- You have a legacy system that you'd rather change only minimally, but you need to mutate its outputs to meet new requirements
- For some reason you don't want to use prolog (which is probably the right tool here)

## How Does It Work?

The idea is that you can keep small snippits of code in the database.  This lets them be region specific and prevents you from increasing complexity _everywhere_ when you have a random requirement _somewhere_.  Essentially, it's a rules engine where the rules are written in groovy and they are defined in terms of some small set of parameters (which you will have to hard-code and push through your slow IDE-to-production pipe).  The inputs for the rules engine are the outputs of the legacy system (though it might need to be tweaked to provide them cleanly).

The test case is also written in groovy, but there's no reason you couldn't also do it in java.

## How do I run it?

I'm not sure what you'd gain from running it, you could just look at my captured output.

### I want to see it work

You'll need both mysql and groovy installed, and you'll need an empty database called `sq`.  To run a groovy script, type `groovy path/to/the/script.groovy`.

You can seed the database with the two required tables `squirrel_tag` and `squirrel_cage` by running [seed.groovy](seed.groovy).
Running [test.groovy](test.groovy) will run the test case.
[output.txt](output.txt) will appear on the terminal.

## I'm Confused

That's ok, we can go over it in person sometime.

## I hate it

That's ok too, I just wanted to know that it was understood before it was dismissed.

