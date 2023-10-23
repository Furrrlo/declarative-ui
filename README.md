[React](https://react.dev/) inspired declarative UI library written in Java. I initially started working
on it 'cause I wanted to learn how it could work internally and while I was at it, I also started
to add reactivity similar to [Solidjs signals](https://www.solidjs.com/). Currently based on
Swing, can probably easily be adapted for use with JFX.

Everything might be broken btw

## Quick start

```java
class Main {
    public static void main(String[] args) {
        Application.create(app -> app.roots(roots -> roots.add(JDFrame.fn(frame -> {
            var counter = frame.useState(0);
            frame.title(() -> "Counter " + counter.get());
            frame.defaultCloseOperation(() -> WindowConstants.EXIT_ON_CLOSE);
            frame.contentPane(JDPanel.fn(panel -> {
                panel.layout(panel.useMemo(FlowLayout::new));
                panel.children(children -> {
                    children.add(JDLabel.fn(label -> label.text(() -> "Current value is " + counter.get())));
                    children.add(JDButton.fn(btn -> {
                        btn.text(() -> "Click me");
                        btn.actionListener(btn.useCallback(evt -> counter.update(i -> i + 1)));
                    }));
                });
            }));
            frame.minimumSize(() -> new Dimension(700, 700));
            frame.size(() -> new Dimension(700, 700));
            frame.locationRelativeTo(() -> null);
            frame.visible(() -> true);
        }))));
    }
}
```

## Hooks

<table>
<thead>
</thead>
<tbody>

<tr>
<td>

```java
<V> State<V> useState(V value);
<V> State<V> useState(V value, BiPredicate<V, V> equalityFn);
<V> State<V> useState(Supplier<V value);
<V> State<V> useState(Supplier<V value, BiPredicate<V, V> equalityFn);
```
</td>
<td>
Adds a state variable to your component, similar to 
<a href="https://react.dev/reference/react/useState">React useState</a>. Additionally, the returned object
is subject to automatic dependency tracking, as with 
<a href="https://www.solidjs.com/docs/latest#createsignal">Solidjs signals</a>, 
which means that calling State#get() within a tracked scope causes the calling function to depend 
on this variable, so it will re-run when this State gets updated. Contrary to Solidjs, components can still
re-render, so calling #get() in a component will cause the component to be a dependant of this variable and
therefore be re-rendered when the State variable gets updated.

Calling the setter sets the State's value and triggers dependents to rerun if the value actually
changed. By default, when calling the setter, the state only updates (and causes dependents to rerun) 
if the new value is different than the old value according to Java `Objects#deepEquals(Object a, Object b)`.
The overloads with `BiPredicate<V, V> equalityFn` allows to change the equality function used for this 
State variable.

TBD: The additional overloads accepting a `Supplier<V> value` are untracked, so that you can access
other tracked variables without causing the parent component to possibly re-render.
</td>
</tr>

<tr>
<td>

```java
<V> Memo<V> useMemo(IdentifiableSupplier<V> value);
<V> Memo<V> useMemo(IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn);
```
</td>
<td>
Lets you cache the result of a calculation between re-renders, same as 
<a href="https://react.dev/reference/react/useMemo">React useMemo</a> but with (optionally) 
automatic dependency tracking (see <a href="identifiables">Identifiables section</a>).

Additionally, like 
<a href="https://www.solidjs.com/docs/latest#creatememo">Solidjs createMemo</a>, 
a memo can shield dependents from updating when the memo's dependencies change but the resulting memo value doesn't. 
Like `useState`, the derived variable updates (and triggers dependents to rerun) only when the value returned by the 
memo function actually changes from the previous value, according to Java `Objects#deepEquals(Object a, Object b)`.
Alternatively, you can use the overload with `BiPredicate<V, V> equalityFn` for testing equality.
</td>
</tr>

<tr>
<td>

```java
<V extends Serializable> V useCallback(V fun);
<V> V useCallbackExplicit(V fun, Object dependency);
<V> V useCallbackExplicit(V fun, List<Object> dependencies);
```
</td>
<td>
Lets you cache a function definition between re-renders, same as 
<a href="https://react.dev/reference/react/useCallback">React useCallback</a> but with (optionally) 
automatic dependency tracking (see <a href="identifiables">Identifiables section</a>).

There is no Solidjs counterpart, as it does not re-render components (see `useState` for more info)
</td>
</tr>

<tr>
<td>

```java
static <V void Memo#indexCollection(
    IdentifiableSupplier<Collection<V> collection,
    BiConsumer<DeclareMemoFn<V, Integer> fn
);
```
</td>
<td>
Allows to iterate a collection (possibly coming from a tracked variable like a State or a Memo)
and offers a function to declare a memo for each value in the collection.

The current tracking scope will only re-run if the size of the given collection changes (additions and removals).
On other changes to the collection (setting a value), only the (possibly) declared memos of elements
will be re-run. The collection supplier is also a tracking function and might be re-run on all changes.

This basically allows to declare components by using indexes of the collection as key.

While this looks similar to <a href="https://www.solidjs.com/docs/latest#indexarray">Solidjs indexArray</a>,
it differs on the fact that it does not directly cache each item by key, as because of component re-renders we have
the same limitation on declaring memos in loops as <a href="https://react.dev/reference/react/useMemo#caveats">React</a>
does.

The suggested approach is to declare a wrapper component, add it as a child specifying the index as key
and declare the value memo inside said wrapper component.
</td>
</tr>

<tr>
<td>

```java
static <V void Memo#mapCollection(
    IdentifiableSupplier<Collection<V> collection,
    BiConsumer<V, DeclareMemoFn<Integer>> fn
);
```
</td>
<td>
Allows to iterate a collection (possibly coming from a tracked variable like a State or a Memo)
and offers a function to declare a memo on child components for each index in the collection.

The current tracking scope will re-run on any collection changes.
The collection supplier is also a tracking function and might also be re-run.

This basically allows to declare components by using values of the collection as key.

While this looks similar to <a href="https://www.solidjs.com/docs/latest#maparray">Solidjs mapArray</a>,
it differs on the fact that it does not directly cache each item, as because of component re-renders we have
the same limitation on declaring memos in loops as <a href="https://react.dev/reference/react/useMemo#caveats">React</a>
does.

The suggested approach is to declare a wrapper component, add it as a child specifying the value (or an id of the value) 
as key and (optionally) declare the index memo inside said wrapper component. Additionally, it is also suggested to 
declare a memo of the value itself in the wrapper, as it will prevent the wrapped component from having to be 
fully re-rendered on each change of the value. Instead, only the wrapper body will be re-run and only tracked attributes
and memos of the wrapped components will be re-run.
</td>
</tr>

<tr>
<td>

```java
<V> DeclarativeComponentContext<T> inner(
    Function<T, V> getter,
    DeclarativeComponent<V component
);
```
</td>
<td>
Lets you extract a field out of a Swing component and decorate it. 

This is useful for dealing with components that decorate other components by creating 
or requesting an instance in the constructor.

For example, given a `NumberedTextComponent` which wraps a `JTextArea` to add line numbers, 
we can declare the component as
```java
JDComponent.fn(() -> new NumberedTextComponent(new RSyntaxTextArea()), p -> { ... })
```
and then in the body call
```java
p.inner(o -> o.getTextArea(), JDTextArea.fn(textArea -> { ... })
```
in order to decorate the underlying text area.
</td>
</tr>

</tbody>
</table>

## Identifiables

In order to automatically track dependencies of a lambda, the library makes heavy use of serializable
lambdas in order to be able to request to Java which local variables are captured by the lambda and use
those as dependencies. 

A prime example is in the `useMemo` hook, where you are not forced to declare the dependencies, as by default
the lambda captured variables will be used as dependencies. The same is also true for component bodies, which
by default are the same as [React memo components](https://react.dev/reference/react/memo#memo).

You can usually tell when a requested function will only be conditionally re-run when its dependencies change
when it's one of the following interfaces:
- `IdentifiableSupplier`
- `IdentifiableConsumer`
- `IdentifiableFunction`
- `IdentifiableBiFunction`
- `<T extends Serializable>` and wants a lambda

You can always manually declare the dependencies by:
- calling the static method `explicit` on one of the interfaces above
- call the overload of the method (usually suffixed with -explicit) which takes a non-serializable lambda
  and a list of dependencies

An identifiable function is not necessarily a tracked function: 
- a tracking function will always be re-run when a tracked value that it uses changes, 
  (even if it's an identifiable function and its dependencies haven't changed).  
- an identifiable function will additionally only re-run if its dependencies changed when
  invoked during a component re-render

That means that:
- An identifiable tracked function will
    - re-run on tracked variable changes
    - conditionally re-run on component re-render
- An identifiable non-tracked function will
    - not be tracked by tracking variables, if you call them inside it, you will cause its 
      parent tracking scope to re-run (which might be a component re-render)
    - conditionally re-run on component re-render
- A non-identifiable tracked function will
    - re-run on tracked variable changes
    - re-run on component re-render

## Missing stuff
- untrack
- effects
