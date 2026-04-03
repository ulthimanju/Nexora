import { cn } from '../../lib/cn'

export const Label = ({ htmlFor, children, required, className, ...rest }) => {
  return (
    <label
      htmlFor={htmlFor}
      className={cn(
        'block text-sm font-medium text-primary mb-1.5',
        className
      )}
      {...rest}
    >
      {children}
      {required && <span className="text-error ml-1">*</span>}
    </label>
  )
}
