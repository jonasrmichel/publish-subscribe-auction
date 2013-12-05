publish-subscribe-auction
=========================

A distributed publish-subscribe auction service written in Java. Features sockets and a self-constructing broker tree.

The auction system consists of <b>Buyers</b>, <b>Sellers</b>, and <b>Brokers</b>. Buyers and sellers both publish and subscribe to events, and brokers mediate between buyers and sellers to distribute events. Brokers "match" events based on their content.

<h1>Usage</h1>
Auction participants (<code>Buyers</code>, <code>Sellers</code>, and <code>Brokers</code>) may be started from the command line.

To start an auction participant from the command line, just do this:
<pre>
<code>$ cd path/to/this/directory</code>
<code>$ java -cp bin edu.courses.middleware.pubsub.[participant]</code>
</pre>

Where [participant] is one of: <code>Broker</code>, <code>Buyer</code>, <code>Seller</code>.

Provided at least one <code>Broker</code> is running, there is no required order for starting additional auction participants. However, if the leaf level of the tree is entirely composed of <code>Buyer</code>s and <code>Seller</code>s then no more auction participants may be added (i.e., the <code>Broker</code> tree does not dynamically grow if more Brokers are needed).

<code>Buyer</code>s and <code>Seller</code>s expose a command line interface (CLI) to the user. Once a <code>Buyer</code>/<code>Seller</code> has been started and has successfully joined the Broker tree as a client you will be prompted to enter a command. At any time you use the command "h" (help) to see a list of  available commands and their usage.

<h1>Troubleshooting</h1>
If the <code>Broker</code> tree's dedicated root port is in use by your system, you may change the static <code>ROOT_BROKER_PORT</code> in the <code>Broker</code> class.

(You will have to recompile the auction system if you change the <code>ROOT_BROKER_PORT</code>.)
