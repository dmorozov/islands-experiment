import 'preact';

declare global {
  namespace JSX {
    interface Element extends preact.JSX.Element {}
    interface ElementClass extends preact.JSX.ElementClass {}
    interface ElementAttributesProperty extends preact.JSX.ElementAttributesProperty {}
    interface ElementChildrenAttribute extends preact.JSX.ElementChildrenAttribute {}
    interface IntrinsicAttributes extends preact.JSX.IntrinsicAttributes {}
    interface IntrinsicClassAttributes<T> extends preact.JSX.IntrinsicClassAttributes<T> {}
    interface IntrinsicElements extends preact.JSX.IntrinsicElements {}
  }
}

// Augment Preact types to be compatible with React
declare module 'preact' {
  namespace preact {
    // Make ComponentChildren compatible with ReactNode
    type ReactNode = ComponentChild;
    type ReactElement = VNode<any>;
    type ReactPortal = VNode<any>;
  }
}

export {};
