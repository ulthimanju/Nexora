import { forwardRef } from 'react'
import { Label } from '../atoms/Label'
import { Input } from '../atoms/Input'
import { ErrorText } from '../atoms/ErrorText'

export const FormField = forwardRef(
  ({ label, name, error, required, children, ...inputProps }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <Label htmlFor={name} required={required}>
            {label}
          </Label>
        )}
        {children || <Input ref={ref} id={name} name={name} error={!!error} {...inputProps} />}
        <ErrorText>{error}</ErrorText>
      </div>
    )
  }
)

FormField.displayName = 'FormField'
