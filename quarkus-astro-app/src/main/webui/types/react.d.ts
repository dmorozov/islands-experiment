declare module 'react' {
  export = preactCompat;
  export as namespace React;
}

declare namespace preactCompat {
  export import Component = preact.Component;
  export import FunctionComponent = preact.FunctionComponent;
  export import ComponentType = preact.ComponentType;
  export import ComponentClass = preact.ComponentClass;
  export import FC = preact.FunctionComponent;
  export import createElement = preact.createElement;
  export import cloneElement = preact.cloneElement;
  export import createContext = preact.createContext;
  export import createRef = preact.createRef;
  export import Fragment = preact.Fragment;
  export import createPortal = preactCompat.createPortal;

  // Hooks
  export import useState = preact.hooks.useState;
  export import useReducer = preact.hooks.useReducer;
  export import useEffect = preact.hooks.useEffect;
  export import useLayoutEffect = preact.hooks.useLayoutEffect;
  export import useRef = preact.hooks.useRef;
  export import useImperativeHandle = preact.hooks.useImperativeHandle;
  export import useMemo = preact.hooks.useMemo;
  export import useCallback = preact.hooks.useCallback;
  export import useContext = preact.hooks.useContext;
  export import useDebugValue = preact.hooks.useDebugValue;
  export import useErrorBoundary = preact.hooks.useErrorBoundary;
  export import useId = preact.hooks.useId;

  // Types
  export import ReactNode = preact.ComponentChildren;
  export import ReactElement = preact.VNode;
  export import JSXElementConstructor = preact.ComponentType;
  export import RefObject = preact.RefObject;
  export import Ref = preact.Ref;
  export import MutableRefObject = preact.RefObject;
  export import Context = preact.Context;
  export import Provider = preact.Provider;
  export import Consumer = preact.Consumer;
  export import ComponentPropsWithRef = preact.ComponentProps;
  export import ComponentPropsWithoutRef = preact.ComponentProps;
  export import ComponentProps = preact.ComponentProps;
  export import PropsWithChildren = preact.RenderableProps;
  export import HTMLAttributes = preact.JSX.HTMLAttributes;
  export import CSSProperties = preact.JSX.CSSProperties;
  export import FormEvent = preact.JSX.TargetedEvent;
  export import ChangeEvent = preact.JSX.TargetedEvent;
  export import MouseEvent = preact.JSX.TargetedMouseEvent;
  export import KeyboardEvent = preact.JSX.TargetedKeyboardEvent;
  export import FocusEvent = preact.JSX.TargetedFocusEvent;
  export import ClipboardEvent = preact.JSX.TargetedClipboardEvent;
  export import TouchEvent = preact.JSX.TargetedTouchEvent;
  export import PointerEvent = preact.JSX.TargetedPointerEvent;

  export type ReactPortal = preact.VNode;
  export type Key = string | number;
  export type LegacyRef<T> = Ref<T>;
  export type ComponentState = any;
  export type ReactInstance = preact.Component;
  export type SyntheticEvent<T = Element, E = Event> = preact.JSX.TargetedEvent<T, E>;

  export interface Attributes {
    key?: Key | null;
  }

  export interface RefAttributes<T> extends Attributes {
    ref?: Ref<T>;
  }

  export interface ClassAttributes<T> extends Attributes {
    ref?: Ref<T>;
  }

  export type ElementType = preact.ComponentType<any> | keyof preact.JSX.IntrinsicElements;

  // Utility type for getting element ref from component
  export type ElementRef<C extends ElementType> = C extends new (
    props: any
  ) => preact.Component<any, any>
    ? preact.Component<any, any>
    : C extends (props: any) => any
      ? ReturnType<C> extends { ref?: infer R }
        ? R extends preact.Ref<infer T>
          ? T
          : never
        : never
      : C extends keyof preact.JSX.IntrinsicElements
        ? preact.JSX.IntrinsicElements[C] extends preact.JSX.HTMLAttributes<infer T>
          ? T
          : never
        : never;

  export type ComponentPropsWithoutRef<T extends ElementType> = preact.ComponentProps<T>;

  export function createPortal(
    children: preact.ComponentChildren,
    container: Element | DocumentFragment,
    key?: string | null
  ): preact.VNode;

  export const Children: {
    map<T, R>(
      children: T | readonly T[],
      fn: (child: T, index: number) => R
    ): R[];
    forEach<T>(
      children: T | readonly T[],
      fn: (child: T, index: number) => void
    ): void;
    count(children: any): number;
    only(children: any): any;
    toArray(children: any): any[];
  };

  export function isValidElement(element: any): boolean;
  export function memo<P = {}>(
    component: FunctionComponent<P>,
    compare?: (prev: P, next: P) => boolean
  ): FunctionComponent<P>;
  export function lazy<T extends ComponentType<any>>(
    loader: () => Promise<{ default: T }>
  ): T;
  export function forwardRef<T, P = {}>(
    render: (props: P, ref: Ref<T>) => preact.ComponentChild
  ): FunctionComponent<P>;

  export function startTransition(callback: () => void): void;
  export function useTransition(): [boolean, (callback: () => void) => void];
  export function useDeferredValue<T>(value: T): T;
  export function useSyncExternalStore<T>(
    subscribe: (onStoreChange: () => void) => () => void,
    getSnapshot: () => T,
    getServerSnapshot?: () => T
  ): T;
  export function useInsertionEffect(
    effect: () => void | (() => void),
    deps?: any[]
  ): void;
}
